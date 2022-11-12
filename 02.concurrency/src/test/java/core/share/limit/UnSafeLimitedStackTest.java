package core.share.limit;

import org.junit.Test;

import static org.junit.Assert.*;

public class UnSafeLimitedStackTest {

    @Test
    public void thread_unsafe_limited_stack1() throws InterruptedException {
        final UnSafeLimitedStack unSafeLimitedStack = new UnSafeLimitedStack();
        OldTree oldTree = null;

        new Thread(() -> {
            unSafeLimitedStack.unsafeLocalVariable(oldTree);
        }).start();

        Thread.sleep(100);

        // 매개변수에 로컬 변수를 할당하는 것은 불가능 한 것 같다.
        // 매개변수에 로컬 변수로 새로운 할당하는 것이 애초부터 불가능한가?
        assertNull(oldTree);
        assertEquals(10, unSafeLimitedStack.getMyOldTree().getAge());
    }

    @Test
    public void thread_unsafe_limited_stack2() throws InterruptedException {
        final UnSafeLimitedStack unSafeLimitedStack = new UnSafeLimitedStack();
        OldTree oldTree = null;

        new Thread(() -> {
            for (int i = 0; i < 1000; ++i) {
                unSafeLimitedStack.unsafeLocalVariable(null);
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 10000; ++i) {
                unSafeLimitedStack.getMyOldTree().ageIncrement();
            }
        }).start();

        Thread.sleep(1000);

        // 정확한 값을 모른다.
        // 즉 스레드 한정인 로컬 변수를 외부에 노출시켜서는 안된다.
        // 외부: 로컬을 벗어난 곳
        assertNotEquals(10000, unSafeLimitedStack.getMyOldTree().getAge());
        System.out.println(unSafeLimitedStack.getMyOldTree().getAge());
    }

}