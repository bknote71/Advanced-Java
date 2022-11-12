package core.share.limit;

import org.junit.Test;

import static org.junit.Assert.*;

public class SafeLimitedStackTest {

    @Test
    public void thread_safe_limited_stack() {
        final SafeLimitedStack safeLimitedStack = new SafeLimitedStack();

        for (int i = 0; i < 10; ++i) {
            new Thread(() -> {
                try {
                    safeLimitedStack.localVariable();
                } catch (Exception e) {
                    // 강제적인 테스트 실패
                    assertEquals(1, 2);
                }
            }).start();
        }
    }

}