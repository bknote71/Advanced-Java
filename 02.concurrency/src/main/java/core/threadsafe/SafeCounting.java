package core.threadsafe;

import core.common.GuardBy;
import core.common.NotThreadSafe;
import core.common.ThreadSafe;

/**
 * 락으로 상태 보호하기:
 * - 단순히 복합 동작 부분을 synchronized 블록으로 감싸는 것으로는 부족하다.
 * 1. 특정 변수에 대한 접근을 조율하기 위해 동기화할 때는 해당 변수에 접근하는 모든 부분을 동기화해야 한다.
 * - 해당 변수에 접근하는 거의 모든 부분을 동기화하였지만 한 곳에서 동기화하지 못한다면 결국 동기화가 깨지게 된다.
 * - 한 곳에서라도 공유 변수에 대하여 동기화하지 못하면 그 공유 변수를 동기화하는 전체 부분에서 동기화가 깨진다.
 *
 * 2. 또한 해당 변수에 접근하는 모든 곳에서 반드시 같은 락을 사용해야 한다.
 * - 다른 락을 사용한다면 변경 가능한 공유 변수에 동시에 접근이 가능해진다!
 * - 즉 동기화가 깨진다.
 *
 * 3. 여러 변수에 대한 불변조건이 있으면 해당 변수들은 모두 같은 락으로 보호해야 한다.
 * - 여러 상태 변수가 하나의 불변 조건에 묶여 있다면, 하나의 변수라도 동기화가 깨지게 된다면 전체 동기화가 깨질 수 있다.
 * - 복합적인 1, 2 조건
 *
 * 락에 대한 규칙은 새로운 메서드나 코드 경로를 추가하면서 실수로 동기화하는 걸 잊기만 해도 쉽게 무너질 수 있다.
 * - 그렇기 때문에 문서화가 중요하다
 *
 * 참고
 * - synchronized: 암묵적인 lock 제공
 */
@ThreadSafe
public class SafeCounting {

    /**
     * 모든 변경할 수 있는 공유 변수는 정확하게 단 하나의 락으로 보호해야 한다.
     * 유지보수하는 사람이 알 수 있게 어느 락으로 보호하고 있는지를 명확하게 표시하라.
     */
    @GuardBy("this")
    private int preCount = 0;
    @GuardBy("this")
    private int postCount = 0;

    public static int iterCount = 100000;

    private boolean broken = false;

    public boolean isBroken() {
        return broken;
    }

    public void safeCountingCallBySynchronized() {
        for (int i = 0; i < iterCount; ++i) {
            synchronized (this) {
                preCount += 1;
                postCount += 1;

                broken = (preCount != postCount ? true : broken);
            }
        }
    }

    private Object lock = new Object();

    @GuardBy("lock")
    private int ePreCount = 0;
    @GuardBy("lock")
    private int ePostCount = 0;

    public void safeCountingCallByExplicitLock() {
        for (int i = 0; i < iterCount; ++i) {
            synchronized (lock) {
                ePreCount += 1;
                ePostCount += 1;

                broken = (preCount != postCount ? true : broken);
            }
        }
    }

    // 잘못된 케이스:
    // 1. 변경 가능한 공유 변수에 접근하는 모든 부분을 동기화하지 못하는 상황
    // - 위에서 여러 count 변수에 대해 동기화를 제공하였다. 하지만 이 메서드에서는 동기화를 제공하지 못한다.
    // - 한 곳에서라도 공유 변수에 대하여 동기화하지 못하면 그 공유 변수를 동기화하는 전체 부분에서 동기화가 깨진다.
    @NotThreadSafe
    public void unsafeSynchronizedCountingByCase1() {
        for (int i = 0; i < iterCount; ++i) {
            preCount += 1;
            postCount += 1;
        }
    }

    // 2. 해당 변수에 접근하는 모든 곳에서 반드시 같은 락을 사용하지 못하는 상황
    // --> 상태 변수에 대해 "동시에" 접근이 가능해진다.
    @NotThreadSafe
    public void unsafeSynchronizedCountingByCase2() {
        for (int i = 0; i < iterCount; ++i) {
            synchronized (lock) {
                preCount += 1;
                postCount += 1;
            }
        }
    }

    // 3.여러 변수에 대한 불변조건이 있으면 해당 변수들은 모두 같은 락으로 보호해야 하는데 그러지 못하는 상황
    // - 1, 2번 조건은 하나의 변수에 대하여, 3번은 여러 변수에 대한 조건
    @NotThreadSafe
    public void unsafeSynchronizedCountingByCase3() {
        for (int i = 0; i < iterCount; ++i) {
            synchronized (this) {
                preCount += 1;
                postCount += 1;
            }

            synchronized (lock) {
                ePreCount += 1;
                ePostCount += 1;
            }

            broken = ((preCount == ePreCount && postCount == ePostCount) ? true : broken);
        }
    }

}
