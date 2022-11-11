package core.threadsafe;

import org.junit.Test;

import static org.junit.Assert.*;

public class UnsafeAdderTest {

    static class AddThread extends Thread {
        public void run() {
            UnsafeAdder.add1000();
        }
    }

    @Test
    public void add1000() throws InterruptedException {
        final AddThread addThread1 = new AddThread();
        final AddThread addThread2 = new AddThread();
        final AddThread addThread3 = new AddThread();

        addThread1.start();
        addThread2.start();
        addThread3.start();

        Thread.sleep(1000);
        
        // race condition 이 발생하기 때문에 3000 임을 보장하지 못한다.
        // 즉 스레드 불안정
        // 더하는 숫자가 커질수록 기대값과 차이가 벌어진다.
        assertNotEquals(3000, UnsafeAdder.getValue());
        System.out.println(UnsafeAdder.getValue());
    }

    static class CompareAndSetThread extends Thread {
        public void run() {
            UnsafeAdder.compareAndAct();
        }
    }

    @Test
    public void compareAndAct() throws InterruptedException {
        final CompareAndSetThread compareAndSetThread1 = new CompareAndSetThread();
        final CompareAndSetThread compareAndSetThread2 = new CompareAndSetThread();
        final CompareAndSetThread compareAndSetThread3 = new CompareAndSetThread();

        compareAndSetThread1.start();
        compareAndSetThread2.start();
        compareAndSetThread3.start();

        Thread.sleep(1000);

        assertNotEquals(30000, UnsafeAdder.getValue());
        System.out.println(UnsafeAdder.getValue());
    }
}