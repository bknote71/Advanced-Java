package core.threadsafe;

import org.junit.Test;

import static org.junit.Assert.*;

public class StatelessAdderTest {


    static class AddThread extends Thread {

        StatelessAdder adder;
        int value;
        Integer integerValue;

        int ret;

        AddThread(StatelessAdder adder, int value) {
            this.adder = adder;
            this.value = value;
        }

        AddThread(StatelessAdder adder, Integer integerValue) {
            this.adder = adder;
            this.integerValue = integerValue;
        }

        @Override
        public void run() {
            ret = adder.add1000(value);
        }
    }

    static class AddThreadToInteger extends Thread {

        StatelessAdder adder;
        Integer integerValue;
        int ret;

        AddThreadToInteger(StatelessAdder adder, Integer integerValue) {
            this.adder = adder;
            this.integerValue = integerValue;
        }

        @Override
        public void run() {
            ret = adder.add10000toInteger(integerValue);
        }
    }

    static class AddThreadToValueHolder extends Thread {

        StatelessAdder adder;
        StatelessAdder.ValueHolder valueHolder;
        int ret;

        AddThreadToValueHolder(StatelessAdder adder, StatelessAdder.ValueHolder valueHolder) {
            this.adder = adder;
            this.valueHolder = valueHolder;
        }

        @Override
        public void run() {
            ret = adder.add10000toValueHolder(valueHolder);
        }
    }


    @Test
    public void add1000() throws InterruptedException {

        final StatelessAdder statelessAdder = new StatelessAdder();

        final AddThread addThread1 = new AddThread(statelessAdder, 1);
        final AddThread addThread2 = new AddThread(statelessAdder, 10);
        final AddThread addThread3 = new AddThread(statelessAdder, 100);

        addThread1.start();
        addThread2.start();
        addThread3.start();

        Thread.sleep(1000);

        assertEquals(1001, addThread1.ret);
        assertEquals(1010, addThread2.ret);
        assertEquals(1100, addThread3.ret);
    }



    @Test
    public void add10000toInteger() throws InterruptedException {
        final StatelessAdder statelessAdder = new StatelessAdder();

        Integer integer = Integer.valueOf(10);

        // integer 로 연산 시 새로운 integer 객체!
        final AddThreadToInteger addThread1 = new AddThreadToInteger(statelessAdder, integer);
        final AddThreadToInteger addThread2 = new AddThreadToInteger(statelessAdder, integer);
        final AddThreadToInteger addThread3 = new AddThreadToInteger(statelessAdder, integer);

        addThread1.start();
        addThread2.start();
        addThread3.start();

        Thread.sleep(1000);

        assertEquals(10010, addThread1.ret);
        assertEquals(10010, addThread2.ret);
        assertEquals(10010, addThread3.ret);
    }

    @Test
    public void add10000toValueHolder() throws InterruptedException {
        final StatelessAdder statelessAdder = new StatelessAdder();

        final StatelessAdder.ValueHolder valueHolder = new StatelessAdder.ValueHolder(0);

        // integer 로 연산 시 새로운 integer 객체!
        final AddThreadToValueHolder addThread1 = new AddThreadToValueHolder(statelessAdder, valueHolder);
        final AddThreadToValueHolder addThread2 = new AddThreadToValueHolder(statelessAdder, valueHolder);
        final AddThreadToValueHolder addThread3 = new AddThreadToValueHolder(statelessAdder, valueHolder);

        addThread1.start();
        addThread2.start();
        addThread3.start();

        Thread.sleep(1000);

        assertNotEquals(30000, addThread1.ret + addThread2.ret + addThread3.ret);
    }

}