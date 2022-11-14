package boundedtype;

import java.util.Arrays;
import java.util.Collection;
import java.util.EmptyStackException;

// PECS: Producer-Extends, Consumer-Super
// - 무언가를 가져와서 쌓고, 쌓은 것을 토대로 소비하는 것
public class Stack<E> {

    private E[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        // E[]: 제네릭 배열을 실제 생성하는 부분에서 사용할 수 없다.
        // E[] => 컴파일 이후 => Object[]
        elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }

        E result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }


    // Producer: 외부로부터 무언가를 가져와서 내부에서 쌓는 과정
    // - Object의 컬렉션에 Number나 Integer를 넣을 수 있다.
    // - Number의 컬렉션에 Integer를 넣을 수 있다.
    public void pushAll(Iterable<? extends E> src) {
        for (E e : src) {
            push(e);
        }
    }

    // Consumer: 내부에 쌓인것을 토대로 다른 곳에 넘겨서 소비하는 과정
    //- Integer의 컬렉션의 객체를 꺼내서 Number의 컬렉션에 담을 수 있다.
    //- Number나 Integer의 컬렉션의 객체를 꺼내서 Object의 컬렉션에 담을 수 있다.
    public void popAll(Collection<? super E> dst) {
        while (!isEmpty()) {
            dst.add(pop());
        }
    }


    public int size() {
        return size;
    }
}
