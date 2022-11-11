package core.threadsafe;

import org.junit.Test;

import static org.junit.Assert.*;

public class UnsafeAtomicAdderTest {

    @Test
    public void unsafeAtomicOperation() throws InterruptedException {
        final UnsafeAtomicCounting unsafeAtomicAdder = new UnsafeAtomicCounting();

        int numOfThread = 100;
        for (int i = 0; i < numOfThread; ++i) {
            new Thread(unsafeAtomicAdder::unsafeCountingCall).start();
        }

        Thread.sleep(1000);

        assertTrue(unsafeAtomicAdder.isFlag());
    }

}