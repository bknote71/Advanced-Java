package api.lockfree;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class LockFreeStack {

    static class Node {
        volatile Node next;
        Object item;

        Node(Object item) {
            this.item = item;
            next = null;
        }

        boolean casNext(Node cmp, Node val) {
            return cmp == next &&
                    NEXT.compareAndSet(this, cmp, val);
        }

        private static final VarHandle NEXT;
        static {
            try {
                MethodHandles.Lookup l = MethodHandles.lookup();
                NEXT = l.findVarHandle(Node.class, "next", Node.class);
            } catch (ReflectiveOperationException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    volatile Node head;
    AtomicInteger size;

    public LockFreeStack() {
        head = new Node(null);
        size = new AtomicInteger();
        size.set(0);
    }

    boolean casHead(Node cmp, Node val) {
        return cmp == head &&
                HEAD.compareAndSet(this, cmp, val);
    }

    public void push(Object item) {
        if (item == null) {
            throw new NullPointerException();
        }

        Node node = new Node(item);

        for (Node cHead = head;;) {
            node.next = cHead;

            if (casHead(head, node))
                break;
        }

        size.incrementAndGet();
    }

    public Object pop() {
        // head 를 head.next 에
        if (head == null) {
            throw new NoSuchElementException();
        }

        Node cHead;
        Node n;
        for (cHead = head, n = cHead.next; ; ) {
            if (n == null) {
                return null;
            }

            if (casHead(cHead, n))
                break;
        }

        size.decrementAndGet();
        return cHead.item;
    }


    private static final VarHandle HEAD;
    static  {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            HEAD = l.findVarHandle(LockFreeStack.class, "head", Node.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public int size() {
        return size.get();
    }

    public void print() {
        for (Node n = head; n != null; n = n.next) {
            System.out.println("item: " + n.item);
        }
    }
}
