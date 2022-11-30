package clone;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MyConcurrentLinkedQueue<E> {

    volatile Node<E> head;
    volatile Node<E> tail;

    public MyConcurrentLinkedQueue() {
        head = tail = new Node<E>();
    }

    static class Node<T> {
        T item;
        Node<T> next;

        Node() {
        }

        Node(T item) {
            this.item = item;
        }

        boolean casItem(T cmp, T val) {
            return ITEM.compareAndSet(this, cmp, val);
        }
    }

    public boolean offer(E e) {
        if (e == null)
            throw new NullPointerException();

        final Node<E> n = new Node<>(e);
        retry:
        for (; ; ) {
            Node<E> t = tail;
            Node<E> p = t, q; // p: current node, q: next node
            // 일정 횟수의 HOP(1) 까지는 tail 쓰기를 하지 않고 읽기만 한다.
            for (int hops = 0; ; ++hops, p = q) {
                q = p.next;
                if (q == null) {
                    if (NEXT.compareAndSet(p, null, n)) {
                        // next에 새로운 노드 삽입 성공 시 tail 도 변경해야함
                        // 이 때 바로 변경하는 것이 아니라 홉 수만큼 기다린다음 변경한다.
                        if (hops >= 1)
                            TAIL.compareAndSet(this, t, n);
                        return true;
                    }
                } else if (hops > 1 && t != tail)
                    continue retry;
            }
        }
    }

    public E poll() {
        Node<E> h = head;
        Node<E> p = h, q;
        for (; ; p = q) {
            E item;
            q = p.next;
            if ((item = p.item) != null && p.casItem(item, null)) {
                HEAD.compareAndSet(this, h, (q != null ? q : p));
                return item;
            } else if (q == null) {
                if (h != p)
                    HEAD.compareAndSet(this, h, p);
                return null;
            }
        }
    }

    public boolean remove(Object o) {
        if (o == null) return false;
        Node<E> p = head, pred = null, q;
        for (; p != null; pred = p, p = q) {
            q = p.next;
            E item;
            if ((item = p.item) != null && o.equals(item) && p.casItem(item, null)) {
                if (q != null) {
                    if (pred != null)
                        NEXT.compareAndSet(pred, p, q);
                    else
                        HEAD.compareAndSet(this, p, q);
                }
                return true;
            }
        }
        return false;
    }


    private static final VarHandle HEAD;
    private static final VarHandle TAIL;
    static final VarHandle NEXT;
    static final VarHandle ITEM;


    static {
        try {
            final MethodHandles.Lookup l = MethodHandles.lookup();
            HEAD = l.findVarHandle(MyConcurrentLinkedQueue.class, "head", Node.class);
            TAIL = l.findVarHandle(MyConcurrentLinkedQueue.class, "tail", Node.class);
            NEXT = l.findVarHandle(Node.class, "next", Node.class);
            ITEM = l.findVarHandle(Node.class, "item", Object.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
