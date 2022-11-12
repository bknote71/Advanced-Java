package core.share.escaped;

import org.junit.Test;

import static org.junit.Assert.*;

public class UnsafeListenerTest {

    @Test
    public void valueBeforeThread() {
        final UnsafeListener unsafeListener = new UnsafeListener();
        assertEquals(1, unsafeListener.getValue());
    }

    @Test
    public void valueAfterThread() throws InterruptedException {
        final UnsafeListener unsafeListener = new UnsafeListener();

        Thread.sleep(200);

        assertEquals(2, unsafeListener.getValue());
    }

    @Test
    public void unsafeConstructor() throws InterruptedException {
        final UnsafeListener unsafeListener = new UnsafeListener(10000);

        Thread.sleep(100);

        // test가 실패할 수도 성공할 수도 있다.
        assertNotEquals(20000, unsafeListener.getValue());
        System.out.println(unsafeListener.getValue());
    }

}