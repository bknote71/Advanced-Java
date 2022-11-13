package core.share.limit;

/**
 * ThreadLocal?
 * - 특정한 스레드 내에서만 "전역적으로 접근"이 가능한 변수이다.
 * - 즉 특정한 스레드에서 전역적으로 접근해도 같은 객체임을 보장한다는 뜻이다.
 */
public class ThreadLocalVariable {

    private int value;

    public ThreadLocalVariable(int value) {
        this.value = value;
    }

    public void add10000() {
        for (int i = 0; i < 10000; ++i) {
            value += 1;
        }
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
