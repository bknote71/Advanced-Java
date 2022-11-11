package core.threadsafe;

import org.junit.Test;

import static org.junit.Assert.*;

public class SafeCountingTest {

    @Test
    public void safeCountBySynchronized() throws InterruptedException {
        final SafeCounting safeCounting = new SafeCounting();

        int numOfThread = 10;
        for (int i = 0; i < numOfThread; ++i) {
            new Thread(() -> safeCounting.safeCountingCallBySynchronized()).start();
        }

        Thread.sleep(1000);
        assertFalse(safeCounting.isBroken());
    }

    @Test
    public void safeCountByLock() throws InterruptedException {
        final SafeCounting safeCounting = new SafeCounting();

        int numOfThread = 10;
        for (int i = 0; i < numOfThread; ++i) {
            new Thread(() -> safeCounting.safeCountingCallByExplicitLock()).start();
        }

        Thread.sleep(1000);
        assertFalse(safeCounting.isBroken());
    }

    @Test
    public void brokenSynchronizedByCase1() throws InterruptedException {
        final SafeCounting safeCounting = new SafeCounting();

        int numOfThread = 10;
        for (int i = 0; i < numOfThread; ++i) {
            new Thread(() -> safeCounting.safeCountingCallBySynchronized()).start();
            new Thread(() -> safeCounting.unsafeSynchronizedCountingByCase1()).start();
        }

        Thread.sleep(1000);
        // 한 곳에서라도 공유 변수에 대하여 동기화하지 못하면 그 공유 변수를 동기화하는 전체 부분에서 동기화가 깨진다.
        assertTrue(safeCounting.isBroken());
    }

    @Test
    public void differentLockByCase2() throws InterruptedException {
        final SafeCounting safeCounting = new SafeCounting();

        int numOfThread = 10;
        for (int i = 0; i < numOfThread; ++i) {
            new Thread(() -> safeCounting.safeCountingCallBySynchronized()).start();
            new Thread(() -> safeCounting.unsafeSynchronizedCountingByCase2()).start();
        }

        Thread.sleep(1000);
        assertTrue(safeCounting.isBroken());
    }

    @Test
    public void brokenSynchronizedByCase3() throws InterruptedException {
        final SafeCounting safeCounting = new SafeCounting();

        int numOfThread = 10;
        for (int i = 0; i < numOfThread; ++i) {
            new Thread(() -> safeCounting.unsafeSynchronizedCountingByCase3()).start();
        }

        Thread.sleep(1000);
        assertTrue(safeCounting.isBroken());
    }
}