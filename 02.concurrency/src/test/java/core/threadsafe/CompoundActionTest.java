package core.threadsafe;

import org.junit.Test;

import static org.junit.Assert.*;

public class CompoundActionTest {

    @Test
    public void unSafeCounting() throws InterruptedException {
        final CompoundAction compoundAction = new CompoundAction();

        int numOfThread = 100;

        for (int i = 0; i < numOfThread; ++i) {
            new Thread(compoundAction::unsafeCountingCall).start();
        }

        Thread.sleep(1000);

        assertNotEquals(numOfThread * CompoundAction.iterCount, compoundAction.getCount());
        System.out.println(compoundAction.getCount());
    }

    @Test
    public void safeCountingByAtomicVariable() throws InterruptedException {
        final CompoundAction compoundAction = new CompoundAction();

        int numOfThread = 100;

        for (int i = 0; i < numOfThread; ++i) {
            new Thread(compoundAction::safeCountingCallByAtomic).start();
        }

        Thread.sleep(1000);

        assertEquals(numOfThread * CompoundAction.iterCount, compoundAction.getAtomicCount());
    }

    @Test
    public void safeCountingBySynchronized() throws InterruptedException {
        final CompoundAction compoundAction = new CompoundAction();

        int numOfThread = 100;

        for (int i = 0; i < numOfThread; ++i) {
            new Thread(compoundAction::safeCountingCallBySynchronized).start();
        }

        Thread.sleep(1000);

        assertEquals(numOfThread * CompoundAction.iterCount, compoundAction.getCount());
    }
}