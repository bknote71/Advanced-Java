package core.threadsafe;

/**
 * 공유 가능하고 변경 가능한 객체에 대하여, 그 객체를 사용한다면(읽고, 쓴다면) 스레드 불안전성할 수 있다.
 * - race condition 이 발생: 공유 가능하고 변경 가능한 객체를 접근할 때 발생
 * - 물론 무조건 읽거나 쓴다고 스레드 불안전하다는 것이 아니라 스레드 불안전할 수 있다는 것.
 * 
 * race condition 예: 변수에 값을 저장하는 작업이 해당 변수의 현재 값과 관련이 있다.
 */
public class UnsafeAdder {
    private static int value = 0;

    public static int getValue() {
        return value;
    }

    public static void add1000() {
        for (int i = 0; i < 1000; ++i) {
            value = value + 1;
        }
    }

    // compare and act: race condition 의 대표 유형
    public static void compareAndAct() {
        for (int i = 0; i < 10000; ++i) {
            value += 1;
            if (value == 5000) {
                System.out.println("value is 5000? " + (value == 5000) + " by thread: " + Thread.currentThread().getName());
            }
        }
    }

}
