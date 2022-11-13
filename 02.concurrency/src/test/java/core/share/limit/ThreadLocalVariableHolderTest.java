package core.share.limit;

import org.junit.Test;

import static org.junit.Assert.*;

public class ThreadLocalVariableHolderTest {

    @Test
    public void threadSafetyTest() throws InterruptedException {
        for (int i = 0; i < 100; ++i) {
            new Thread(() -> {
                final ThreadLocalVariable instance = ThreadLocalVariableHolder.getInstance();
//                for (int j = 0; j < 2; ++j) {
//                    instance.add10000();
//                    assertEquals((j + 1) * 10000, instance.getValue());
//                }
                ThreadLocalVariableHolder.clearInstance();
            }).start();
        }

        Thread.sleep(1000);

    }

}