package core.share.limit;

import org.junit.Test;

import static org.junit.Assert.*;

public class SafeLimitedModuleTest {


    @Test
    public void 주먹구구식_한정() throws InterruptedException {
        final SafeLimitedModule safeLimitedModule = new SafeLimitedModule();

        new SafeLimitedModule.WriterThread(safeLimitedModule).start();

        for (int i = 0; i < 10; ++i) {
            new SafeLimitedModule.ReaderThread(safeLimitedModule).start();
        }

        Thread.sleep(100);

        assertFalse(SafeLimitedModule.notSafe);
    }
}