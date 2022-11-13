package core.share.limit;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadLocalVariableHolder {

    private static final ThreadLocal<ThreadLocalVariable> threadLocal = new ThreadLocal<>();

    // ThreadLocalRandom: Thread별로 격리된 random number generator
    private static final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

    public static ThreadLocalVariable getInstance() {

        // 스레드마다 다른 객체
        ThreadLocalVariable threadLocalVariable = threadLocal.get();

        if (threadLocalVariable == null) {
            threadLocalVariable = createInstance();
            threadLocal.set(threadLocalVariable);
        }

        return threadLocalVariable;
    }

    private static ThreadLocalVariable createInstance() {
        return new ThreadLocalVariable(threadLocalRandom.nextInt(1));
    }

    public static void setInstance(ThreadLocalVariable instance) {
        threadLocal.set(instance);
    }

    // 쓸데없는 ThreadLocal 남용
    // 사실상 위의 메서드와 완전히 동일.
    // 즉 호출하는 입장에서 같은 객체를 넘기면 스레드마다 같은 객체가 저장되고
    // 다른 객체를 넘기면 스레드마다 다른 객체가 저장되는 것 뿐이다.
    public static void setInstance2(ThreadLocal<ThreadLocalVariable> instance) {
        threadLocal.set(instance.get());
    }

    // 중요: 스레드풀을 사용하는 환경에서는 스레드의 사용을 마치고 반드시 clear/remove 작업을 해야한다.
    public static void clearInstance() {
        // 자신의 스레드 전용/한정 객체가 clear 된다.
        threadLocal.remove();
    }

}
