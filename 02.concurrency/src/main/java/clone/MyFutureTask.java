package clone;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

// 많은 스레드에서 접근할 수 있다.
// 하지만 하나의 스레드에서만 동작된다. (나머지 스레드는 무시됨)
public class MyFutureTask<T> implements Runnable {

    // task state: 작업을 상태로 관리
    // transitions from these intermediate to final states use cheaper ordered/lazy
    // writes because values are unique and cannot be further modified.
    // 다수의 스레드가 접근하여도 이 작업의 결과는 오직 하나
    // 여러 스레드가 이 작업에 동시에 접근하는 것을 작업 상태로 조율한다.
    // read/write에 따라서 상태 역할이 달라진다.
    // read(get()): 어떤 스레드도 일기 가능
    // write(run()): 하나의 스레드만 쓰기 가능
    // 위 작업을 상태로 조율한다!!
    // 여러개의 순서가 있는 상태? 흐름이 있는 상태는 int로 관리하면 "비교"연산자를 활용하여 더 상황에 맞게 상태 정보를 활용할 수 있다.

    private volatile int state;
    private static final int NEW          = 0;
    private static final int COMPLETING   = 1;
    private static final int NORMAL       = 2;
    private static final int EXCEPTIONAL  = 3;
    private static final int CANCELLED    = 4;
    private static final int INTERRUPTING = 5;
    private static final int INTERRUPTED  = 6;

    private Callable<T> callable;
    private Object result;

    // 쓰기 작업 중인 쓰레드 << 단 1개
    // 여러 쓰레드가 동시에 접근할 수 있어서 cas를 활용해야한다.
    private volatile Thread runner;

    // 읽기 작업 + 아지 작업이 안끝나서 대기중인 쓰레드
    // 이거 역시 여러 쓰레드가 동시에 접근할 수 있어서 cas 활용
    private volatile WaitNode waiters;

    public MyFutureTask(Callable<T> callable) {
        this.callable = callable;
    }

    public MyFutureTask(Runnable runnable, T result) {
        this.callable = new RunnableAdapter<>(runnable, result);
    }


    // weakCAS 는 뭘까..?
    @Override
    public void run() {
        if (state != NEW || !RUNNER.weakCompareAndSet(this, null, Thread.currentThread()))
            return;
        Callable<T> c = callable;
        if (c != null && state == NEW) {
            try {
                Object o = c.call(); // 쓰기 작업은 오직 하나의 쓰레드만 하므로 동기화할 필요가 없다
                set(o);
            } catch (Exception e) {
                setException(e);
            }
        } 
        
        // 최종
    }

    private void set(Object o) {
        // result << o and set state NORMAL
        if (STATE.compareAndSet(this, NEW, COMPLETING)) {
            result = o;
            STATE.setRelease(this, NORMAL);
            finishCompletion();
        }
    }

    private void setException(Exception e) {
        if (STATE.compareAndSet(this, NEW, COMPLETING)) {
            result = e;
            STATE.setRelease(this, EXCEPTIONAL);
            finishCompletion();
        }
    }

    // removes and signals all waiting threads, invokes done(), and nulls out callable
    // waiters에는 여러 스레드가 동시에 접근할 수 있기 때문에 for + cas 알고리즘을 적용해야한다.
    private void finishCompletion() {
        for (WaitNode q = waiters; q != null;) {
            if (WAITERS.compareAndSet(this, q, null)) {
                for (;;) {
                    // 스레드를 깨어야 한다.
                    Thread t = q.thread;
                    if (t != null) {
                        q.thread = null;
                        LockSupport.unpark(t);
                    }
                    // 지금 노드 remove, 다음 노드로 연결
                    WaitNode p = q.next;
                    if (p == null)
                        break;
                    q.next = null;
                    q = p;
                }
                break;
            }
        }
    }

    // 결과를 리턴, 예외면 ExecutionException으로 감싸서 리턴
    // cancel 은 나중에
    public T report(int s) throws ExecutionException {
        if(s == NORMAL)
            return (T) result;
        throw new ExecutionException((Throwable) result);
    }


    public T get() throws ExecutionException, InterruptedException {
        int s = state;
        // s <= COMPLETING: 대기
        if (s <= COMPLETING)
            s = awaitDone(false, 0);
        return report(s);
    }

    private int awaitDone(boolean timed, long nanos) throws InterruptedException {
        long startTime = 0;
        WaitNode d, q = null;
        boolean queued = false;
        for (;;) {
            int s = state;
            d = waiters;

            if (s > COMPLETING) {
                if (q != null) {
                    q.thread = null; // 나중에 지워질 녀석
                }
                return s;
            }
            else if (s == COMPLETING)
                Thread.yield(); // 이건 왜 하는 건지?
            else if (Thread.interrupted()) {
                removeWaiter(q);
                throw new InterruptedException();
            } else if (q == null) {
                // WaitNode 생성
                q = new WaitNode();
            } else if (!queued)
                queued = WAITERS.compareAndSet(this, q.next = d, q);
            else if (timed) {
                long parkNanos = 0;
                if (startTime == 0) { // 첫번째 진입 >> startTime init
                    startTime = System.nanoTime();
                    parkNanos = nanos;
                } else {
                    long elapsed = System.nanoTime() - startTime;
                    if (elapsed >= nanos) {
                        removeWaiter(q);
                        return state;
                    }
                    parkNanos = nanos - elapsed;
                }
                if (state < COMPLETING)
                    LockSupport.parkNanos(this, parkNanos);
            } else
                LockSupport.park(this);
        }

    }

    public void removeWaiter(WaitNode q) {
        // 인터럽트 or 시간 초과 ==> 해당하는 스레드를 null 처리해주면 된다.
        // null 처리하고 해당 노드를 unlink 및 이전 노드는 next 노드를 봐야한다.
        if (q != null) {
            q.thread = null; // null 처리 완료,

            // 1. find cur.thread == null;
            // 2. prev << next (2가지 case: prev 가 null)
            retry:
            for (WaitNode prev = null, cur = waiters, next;
                 cur != null; cur = next) {
                next = cur.next;
                if (cur.thread != null) {
                    prev = cur;
                } else if (prev != null) {
                    prev.next = next;
                    if (prev.thread == null)
                        continue retry;
                } else  // prev == null, cur == waiters
                    if (!WAITERS.compareAndSet(this, cur, next))
                        continue retry;
            }
        }
    }

    private static class RunnableAdapter<T> implements Callable<T> {
        private final Runnable task;
        private final T result;

        RunnableAdapter(Runnable task, T result) {
            this.task = task;
            this.result = result;
        }

        public T call() {
            task.run();
            return result;
        }
    }

    private static class WaitNode {
        Thread thread;
        WaitNode next;
        WaitNode() {
            thread = Thread.currentThread();
        }
    }

    // cas 알고리즘을 적용할 대상: state, runner, waiters
    private static final VarHandle STATE;
    private static final VarHandle RUNNER;
    private static final VarHandle WAITERS;
    static {
        try {
            final MethodHandles.Lookup l = MethodHandles.lookup();
            STATE = l.findVarHandle(MyFutureTask.class, "state", int.class);
            RUNNER = l.findVarHandle(MyFutureTask.class, "runner", Thread.class);
            WAITERS = l.findVarHandle(MyFutureTask.class, "waiters", WaitNode.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
