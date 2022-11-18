package clone;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.locks.LockSupport;

public class SynchronousQueue {

    abstract static class Transferer<E> {
        abstract E transfer(E e, boolean timed, long nanos);
    }

    static final int MAX_TIMED_SPINS =
            (Runtime.getRuntime().availableProcessors() < 2) ? 0 : 32;

    static final int MAX_UNTIMED_SPINS = MAX_TIMED_SPINS * 16;
    static final long SPIN_FOR_TIMEOUT_THRESHOLD = 1000L;

    static class TransferStack<E> extends Transferer<E> {

        // mode
        static final int REQUEST = 0; // 데이터를 요청
        static final int DATA = 1; // 데이터를 건네줌
        static final int FULFILLING = 2; // 스레드 매칭

        static final class Node {
            volatile Node next;
            volatile Node match;
            volatile Thread waiter;

            Object item;
            int mode;
            // item 을 전달하고, mode 를 변경하는 것은
            // volatile/atomic operation 전에 쓰여지거나 후에 읽거나.. ?


            public Node(Object item) {
                this.item = item;
            }

            // next 변경: next가 cmp일 때
            boolean casNext(Node cmp, Node val) {
                return NEXT.compareAndSet(this, cmp, val);
            }


            static final VarHandle NEXT;
            static final VarHandle MATCH;

            static {
                try {
                    final MethodHandles.Lookup lookup = MethodHandles.lookup();
                    NEXT = lookup.findVarHandle(Node.class, "next", Node.class);
                    MATCH = lookup.findVarHandle(Node.class, "match", Node.class);
                } catch (ReflectiveOperationException e) {
                    throw new ExceptionInInitializerError(e);
                }
            }

            public boolean tryMatch(Node n) { // n.next == m
                if (MATCH.compareAndSet(this, null, n)) {
                    // 스레드 깨우기 ㅎㅎ
                    Thread t = waiter;
                    if (t != null) {
                        waiter = null;
                        LockSupport.unpark(t);
                    }
                    return true;
                }
                return match == n;
            }
        }

        volatile Node head;

        boolean casHead(Node h, Node nh) {
            return HEAD.compareAndSet(this, h, nh);
        }

        static Node node(Node n, Object o, Node next, int mode) {
            if (n == null) n = new Node(o);
            n.next = next;
            n.mode = mode;
            return n;
        }


        @Override
        E transfer(E e, boolean timed, long nanos) {

            // new Node: e가 null이 아니면 DATA, null이면 REQUEST
            int mode = (e == null ? REQUEST : DATA);
            Node n = null;

            for (;;) {
                // 1. mode가 같다: push (head를 newNode로 변경)
                Node h = head;
                if (h == null || h.mode == mode) {
                    if (casHead(h, n = node(n, e, h, mode))) {
                        // 대기: waiter thread, spin/block
                        // m: 매치한 노드
                        Node m = awaitFulfill(n, timed, nanos);

                        return (E) (mode == DATA ? n.item : m.item);
                    }
                } else if ((h.mode & FULFILLING) == 0) { // 2. mode도 다르고 fulfill도 아니다.
                    // fulfill 모드 추가하여 push: casHead(h, newNode);
                    if (casHead(h, n = node(n, e, h, mode | FULFILLING))) { // matching...

                        Node m = n.next;
                        Node mn = m.next;

                        // n과 m match
                        // match 가 성공하면 n,m pop == mn을 head 로 변경
                        if (m.tryMatch(n)) {
                            casHead(n, mn);
                            return (E) (mode == DATA ? n.item : m.item);
                        }
                    }
                } else { // 다른 애가 fulfill 중: 기다린다
                    // waiting ..
                }
            }
        }

        private Node awaitFulfill(Node n, boolean timed, long nanos) {
            // spin vs block
            // spin: 말 그대로 loop 반복
            // block: LockSupport.park pr LockSupport.parkNanos() 나노초만큼 대기?
            // fulfill 을 대기하는데 모드가 다른 상대측에서 waiter 스레드를 꺼내고 LockSupport.unpark(thread) 해주면 블락 통과
            
            // 대기하다가 n.match 가 null 이 아니면 된다.

            // timed true: 특정 시간만큼 주차, false: 무제한 주차
            final long deadline = timed ? System.nanoTime() + nanos : 0L;
            Thread t = Thread.currentThread();

            // spin: 짧은 시간이 예상되면 spin
            int spins = shouldSpin(n)
                    ? (timed ? MAX_TIMED_SPINS : MAX_UNTIMED_SPINS)
                    : 0;

            for (;;) {
                Node m = n.match;

                if (m != null)
                    return m;

                if(timed) { // nanos 갱신
                    nanos = deadline - System.nanoTime();
                    if (nanos <= 0) {
                        // ? 취소 ?
                    }
                }

                if (spins > 0) {
                    Thread.onSpinWait(); // 이건 뭐죠?
                    spins = shouldSpin(n) ? (spins - 1) : 0;
                }
                else if (n.waiter == null)
                    n.waiter = t;
                else if (!timed) // 무제한 주차
                    LockSupport.park();
                else if (nanos > SPIN_FOR_TIMEOUT_THRESHOLD)
                    LockSupport.parkNanos(this, nanos);
            }
        }

        // node 가 head 이거나 fulfill 중이면?
        private boolean shouldSpin(Node n) {
            Node h = head;
            return (h == n || (h.mode & FULFILLING) != 0);
        }

        private static final VarHandle HEAD;

        static {
            try {
                final MethodHandles.Lookup lookup = MethodHandles.lookup();
                HEAD = lookup.findVarHandle(Transferer.class, "head", Node.class);
            } catch (ReflectiveOperationException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

}
