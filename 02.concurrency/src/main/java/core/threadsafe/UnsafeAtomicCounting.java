package core.threadsafe;

import core.common.NotThreadSafe;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-Safe:
 * - 여러 스레드에서 수행되는 작업의 타이밍이나 스케쥴링에 따른 교차 실행과 관계 없이 불변조건이 유지돼야 스레드에 안전하다.
 * <p>
 * 복합 동작: 스레드 불안정할 수 있는 동작
 * - 변수를 읽고 수정하기, 점검 후 행동, 관련 있는 변수들의 연산
 * <p>
 * 단일 연산들의 복합 동작:
 * - 하나의 복합 동작을 단일 연산으로 만들어도 스레드 안정하지 않을 수 있다.
 * - 스레드 안전한 여러 단일 연산을 가지고 복합적으로 동작하는 복합 동작에 경우 스레드 안정하지 않다.
 * - 단일 연산으로 복합 동작을 구성할 수 있다.
 * - 즉 모든 스레드 불안정할 수 있는 복합 동작을 단일 연산으로 만들어야 한다.
 * <p>
 * 결론: 상태를 일관성있게 유지하려면 관련 있는 변수들을 하나의 단일 연산으로 갱신해야 한다.
 */
public class UnsafeAtomicCounting {

    private AtomicInteger preCount = new AtomicInteger(0);
    private AtomicInteger postCount = new AtomicInteger(0);

    public static int iterCount = 100000;

    private boolean flag = false;

    public boolean isFlag() {
        return flag;
    }

    @NotThreadSafe
    public void unsafeCountingCall() {
        for (int i = 0; i < iterCount; ++i) {

            preCount.set(i);
            postCount.set(i);

            flag = (preCount.get() != postCount.get() ? true : flag);
        }
    }

}
