package core.threadsafe;

import core.common.NotThreadSafe;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 경쟁 조건(Race Condition): 여러 스레드가 공유하는 변수에 동시적으로 접근할 때 발생
 *
 * 1. Check and Act: 가장 일반적인 경쟁 조건 형태는 잠재적으로 유효하지 않은 값을 참조해서 다음에 뭘 할지를 결정하는 점검 후 행동 형태의 구문이다.
 * - 어떤 사실을 확인하고 그 관찰에 기반해 행동을 한다.
 * - 하지만 해당 관찰은 관찰한 시각과 행동한 시각 사이에 더 이상 유효하지 않게 되었을 수도 있다.
 *
 * 2. 읽고 수정하기: 변수에 값을 저장하는 작업이 해당 변수의 현재 값과 관련이 있는 작업일 경우
 * - value = value + 1;
 */
@NotThreadSafe
public class LazyInitRace {

    private Object instance = null;
    private AtomicInteger value = new AtomicInteger(0);

    // Lazy Initialization: check and act 의 대표적인 패턴
    public Object getInstance() {
        if (instance == null) {
            System.out.println("한 번만 출력되어야 합니다.");
            value.incrementAndGet();
            instance = new Object();
        }
        return instance;
    }


    public int getValue() {
        return value.get();
    }
}
