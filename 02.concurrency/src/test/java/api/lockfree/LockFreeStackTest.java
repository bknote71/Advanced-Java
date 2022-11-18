package api.lockfree;

import org.junit.Test;

import static org.junit.Assert.*;

public class LockFreeStackTest {

    @Test
    public void push() {
        final LockFreeStack lockFreeStack = new LockFreeStack();

        lockFreeStack.push(1);
        lockFreeStack.push(2);
        lockFreeStack.push(3);

        lockFreeStack.print();
    }

    @Test
    public void pop() {
        final LockFreeStack lockFreeStack = new LockFreeStack();

        assertNull(lockFreeStack.pop());
        assertEquals(0, lockFreeStack.size());

        lockFreeStack.push(1);
        lockFreeStack.push(2);
        lockFreeStack.push(3);

        assertEquals(3, lockFreeStack.size());

        lockFreeStack.pop();
        assertEquals(2, lockFreeStack.size());

        lockFreeStack.pop();
        assertEquals(1, lockFreeStack.size());

        lockFreeStack.pop();
        assertEquals(0, lockFreeStack.size());
    }

}