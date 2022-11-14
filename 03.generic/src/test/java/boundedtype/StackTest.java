package boundedtype;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class StackTest {

    @Test
    public void pushAndPop() {
        Stack<Number> stack = new Stack<>();
        Integer value = Integer.valueOf(1);

        stack.push(value);

        Number pop = stack.pop();

        assertEquals(value, pop);
        assertTrue(stack.isEmpty());
    }

    @Test
    public void pe() {
        Stack<Number> stack = new Stack<>();
        Iterable<Integer> ints = Arrays.asList(1, 2, 3);

        stack.pushAll(ints);

        assertTrue(stack.size() == 3);
    }

    @Test
    public void cs() {
        Stack<Integer> stack = new Stack<>();
        Iterable<Integer> ints = Arrays.asList(1, 2, 3);

        stack.pushAll(ints);
        Collection<Number> dst1 = new ArrayList<>();
        Collection<Object> dst2 = new ArrayList<>();

        stack.popAll(dst1);
        assertEquals(3, dst1.size());

        stack.pushAll(ints);
        stack.popAll(dst2);
        assertEquals(3, dst2.size());
    }

}