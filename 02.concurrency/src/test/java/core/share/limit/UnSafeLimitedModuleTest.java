package core.share.limit;

import org.junit.Test;

import static org.junit.Assert.*;

public class UnSafeLimitedModuleTest {

    @Test
    public void 주먹구구식_한정_실패() throws InterruptedException {
        final UnSafeLimitedModule unSafeLimitedModule = new UnSafeLimitedModule();

        new UnSafeLimitedModule.WriterThread(unSafeLimitedModule).start();
        for (int i = 0; i < 10; ++i) {
            new UnSafeLimitedModule.ReadAndCompareThread(unSafeLimitedModule).start();
        }

        Thread.sleep(100);

        assertTrue(UnSafeLimitedModule.notSafe);
    }

}