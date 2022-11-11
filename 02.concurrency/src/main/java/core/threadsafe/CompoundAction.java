package core.threadsafe;

import core.common.NotThreadSafe;
import core.common.ThreadSafe;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 스레드 안정성을 보장하기 위해 점검 후 행동과 읽고 수정하기(쓰기) 등의 작업은 항상 단일 연산이어야 한다.
 * 이런 일련의 동작을 복합 동작(compound action) 이라고 한다.
 * 즉, 스레드에 안전하기 위해서는 전체가 단일 연산으로 실행돼야 하는 일련의 동작을 지칭한다.
 * <p>
 * java.util.concurrent.atomic 패키지
 * - 숫자나 객체 참조 값에 대해 상태를 단일 연산으로 변경할 수 있도록 단일 연산 변수(atomic variable) 클래스가 준비돼 있다.
 */
public class CompoundAction {

    private int count = 0;
    private AtomicInteger atomicCount = new AtomicInteger(0);

    public static int iterCount = 10000;


    @NotThreadSafe
    public void unsafeCountingCall() {
        for (int i = 0; i < iterCount; ++i) {
            count += 1;
        }
    }

    @ThreadSafe
    public void safeCountingCallByAtomic() {
        for (int i = 0; i < iterCount; ++i) {
            atomicCount.incrementAndGet();
        }
    }

    @ThreadSafe
    public synchronized void safeCountingCallBySynchronized() {
        for (int i = 0; i < iterCount; ++i) {
            count += 1;
        }
    }

    public int getCount() {
        return count;
    }

    public int getAtomicCount() {
        return atomicCount.get();
    }

    public void reset() {
        count = 0;
        atomicCount.set(0);
    }


}
