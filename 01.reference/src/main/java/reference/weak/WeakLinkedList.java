package reference.weak;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.WeakHashMap;

/**
 * primitive, 래퍼타입(String, Integer ..) 같은 값은 jvm 내부(상수풀)에 캐싱이 된다.
 * reference가 참조하는 객체(String, Integer, ..)를 null로 할당하여도 상수풀에 존재하기 때문에 WeakReference가 참조하는 객체는 null이 되지 않는다.
 * WeakHashMap, WeakLinkedList 의 key는 레퍼 타입을 사용하면 안되고 레퍼런스 타입을 하나 만들어서 사용해야 한다.
 */
public class WeakLinkedList<K, V> {

    private Node<K, V> head;
    private int size = 0;
    private ReferenceQueue<Object> rq = new ReferenceQueue<>();

    public int size() {
        return size;
    }

    public void clean() {
        System.out.println("cleaning...");

        for (Object o; (o = rq.poll()) != null; ) {
            Node<K, V> e = (Node<K, V>) o;
            Node<K, V> t = head;
            Node<K, V> prev = null;

            for (; t != null; ) {
                if (eq(e, t)) {
                    if (prev != null)
                        prev.next = t.next;
                    else
                        head = t.next;

                    o = e = t = null;
                    size--;
                    break;
                }
                prev = t;
                t = t.next;
            }
        }
    }

    // 애초부터 null key인 것과 null로 변경된 key를 구분한다.
    // 애초부터 null인 key
    private static final Object NULL_KEY = new Object();

    private static Object maskNull(Object key) {
        return (key == null) ? NULL_KEY : key;
    }

    static Object unmaskNull(Object key) {
        return (key == NULL_KEY) ? null : key;
    }

    // key, t.get(): key는 null이 아니고 t.get이 null일 수가 있기 때문에 순서 고정
    private static boolean eq(Object x, Object y) {
        return x == y || x.equals(y);
    }

    public V get(K key) {
        Object k = maskNull(key);

        for (Node<K, V> t = head; t != null; t = t.next) {
            if (eq(k, t.get())) {
                return t.value;
            }
        }
        return null;
    }

    // 맨 뒤에 put
    public V put(K key, V value) {
        Object k = maskNull(key);

//        if (head == null) {
//            head = node;
//            size = 1;
//        }

        Node<K, V> t = head;
        Node<K, V> prev = null;

        // 탐색하여서 같은 것이 있으면 반환
        for (; t != null; ) {
            if (eq(k, t.get())) {
                V oldValue = t.value;
                if (t.value != value) {
                    t.value = value;
                }
                return oldValue;
            }
            prev = t;
            t = t.next;
        }
        // prev: null or end

        // new put
        Node<K, V> node = new Node<>(k, value, rq);
        if (prev == null && head == null) { // head == null
            head = node;
        } else {
            prev.next = node;
        }

        size++;

        return null;
    }

    public boolean remove(K key) {
        Object k = maskNull(key);

        if (head == null) {
            return false;
        }

        Node<K, V> t = head;
        Node<K, V> prev = null;

        for (; t != null; ) {
            if (eq(k, t.get())) {
                if (prev != null)
                    prev.next = t.next;
                else
                    head = t.next;

                t = null;
                size--;
                return true;
            }
            prev = t;
            t = t.next;
        }
        return false;
    }

    public void print() {
        for (Node<K, V> t = head; t != null; t = t.next) {
            System.out.println(t.value + " ");
        }
    }

    static class Node<K, V> extends WeakReference<Object> {
        V value;
        Node<K, V> next;

        public Node(Object referent, V value, ReferenceQueue<Object> q) {
            super(referent, q);
            this.value = value;
        }

        public K getKey() {
            return (K) WeakLinkedList.unmaskNull(get());
        }

        public V getValue() {
            return value;
        }
    }
}
