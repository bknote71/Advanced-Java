package clone;

import java.util.HashSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

public class MyTaskPoolExecutor {

    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int COUNT_MASK = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    private static final int RUNNING = -1 << COUNT_BITS;
    private static final int SHUTDOWN = 0 << COUNT_BITS;
    private static final int STOP = 1 << COUNT_BITS;
    private static final int TIDYING = 2 << COUNT_BITS;
    private static final int TERMINATED = 3 << COUNT_BITS;

    // Packing and unpacking ctl
    private static int runStateOf(int c) {
        return c & ~COUNT_MASK;
    }

    private static int workerCountOf(int c) {
        return c & COUNT_MASK;
    }

    private static int ctlOf(int rs, int wc) {
        return rs | wc;
    }


    private static boolean runStateLessThan(int c, int s) {
        return c < s;
    }

    private static boolean runStateAtLeast(int c, int s) {
        return c >= s;
    }

    private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }

    private boolean compareAndIncrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect + 1);
    }

    private boolean compareAndDecrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect - 1);
    }

    private void decrementWorkerCount() {
        ctl.addAndGet(-1);
    }

    // 작업 대기 큐
    private final BlockingQueue<Runnable> workQueue;

    // concurrent workers set 용 lock
    private final ReentrantLock mainLock = new ReentrantLock();
    private final HashSet<Worker> workers = new HashSet<>();

    private final int corePoolSize;
    private final int maximumPoolSize;
    private final long keepAliveTime;
    private final ThreadFactory threadFactory;

    public ThreadFactory getThreadFactory() {
        return this.threadFactory;
    }

    private final class Worker
            extends AbstractQueuedSynchronizer
            implements Runnable {

        final Thread thread;
        // 처음만 스레드에서 실행할 작업
        Runnable firstTask;
        volatile long completedTasks;

        public Worker(Runnable firstTask) {
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }

        @Override
        public void run() {
            runWorker(this);
        }

        public void lock() {

        }

        public void unlock() {

        }
    }

    public MyTaskPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                Executors.defaultThreadFactory());
    }

    public MyTaskPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory) {
        // validate argument

        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
    }

    public void execute(Runnable runnable) {
        if (runnable == null) throw new NullPointerException();

        // 1. corePoolSize보다 워커의 개수가 작으면 워커를 추가한다.
        // 2. 코어의 개수보다 워커의 개수가 많거나 같으면 작업 대기큐(워커 큐)에 넣는다.
        // 3. 실패하면 새로운 스레드로 스레드 풀(workers)에 추가한다.
        // 모두 실패 시 reject()
        int c = ctl.get();
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(runnable, true)) {
                return;
            }
            c = ctl.get();
        }
        if (isRunning(c) && workQueue.offer(runnable)) {
            // 작업 대기 큐에 넣는 것을 성공하였다면 다시 한 번 상태 체크
        } else if (!addWorker(runnable, false))
            reject(runnable);
    }

    private boolean addWorker(Runnable firstTask, boolean core) {
        // 워커를 스레드 풀에 새롭게 추가
        // shutdown 상태: 더이상 작업을 받을 수 없으며, 워크큐에 있는 작업을 완료시킨다.
        // shutdown 
        // + (stop(작업 처리 못함)||firstTask!=null(새로운 작업 처리 못함)||workQueue.isEmpty()(처리할 작업 없음))
        retry:
        for (; ; ) {
            int c = ctl.get();
            if (runStateAtLeast(c, SHUTDOWN)
                    && (runStateAtLeast(c, STOP) || firstTask != null || workQueue.isEmpty()))
                return false;

            for (; ; ) { // cas + for + 상태로 제어하는 객체 ==> 매 for 문마다 기준이 되는 상태를? 체크.
                if (compareAndIncrementWorkerCount(c))
                    break retry;
                c = ctl.get();
                if (runStateAtLeast(c, SHUTDOWN))
                    continue retry;
            }
        }

        boolean workerStarted = false;
        boolean workerAdded = false;
        Worker w = null;

        w = new Worker(firstTask);
        final Thread thread = w.thread;
        if (thread != null) {
            // workers <- worker 추가
            // workers 는 mainLock을 사용하여 동시성 제어
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                int c = ctl.get();

                if (isRunning(c)) {
                    if (thread.getState() == Thread.State.NEW)
                        throw new IllegalThreadStateException(); // thread factory failure
                    workers.add(w);
                    workerAdded = true;
                }
            } finally {
                mainLock.unlock();
            }
            if (workerAdded) {
                thread.start();
                workerStarted = true;
            }
        }
        return workerStarted;
    }

    private void reject(Runnable runnable) {

    }

    private void runWorker(Worker w) {
        // w의 스레드에서 w 실행
        // w의 작업: 1. firstTask 실행 2. 다음부터 워커큐에 있는 작업을 땡겨서 실행한다.
        final Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock();
        try {
            while (task != null || (task = getTask()) != null) {
                w.lock();
                try {
                    task.run();
                } catch (Throwable ex) {
                    throw ex;
                } finally {
                    task = null;
                    ++w.completedTasks;
                    w.unlock();
                }
            }
        } finally {
            processWorkerExit(w);
        }
    }

    private Runnable getTask() {
        for (; ; ) {
            int c = ctl.get();

            // check if queue empty
            if ((runStateAtLeast(c, STOP) || workQueue.isEmpty())
                    && runStateAtLeast(c, SHUTDOWN)) {
                // running 이면 실행중이니까 워커 카운트를 줄이면 안된다.
                // SHUTDOWN 이상 + empty 이면 줄 작업이 없고 따라서
                // getTask() 로 작업을 대기중인 워커가 쓸데가 없어진다.
                decrementWorkerCount();
                return null;
            }

            try {
                Runnable task = workQueue.take();
                if (task != null)
                    return task;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processWorkerExit(Worker w) {
        // workers에서 w를 제거한다.
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            workers.remove(w);
        } finally {
            mainLock.unlock();
        }

        tryTerminated();
    }

    private void tryTerminated() {
        // 종료 작업 >> 상태 변환
        // 1. TIDYING 2. TERMINATED
    }

}
