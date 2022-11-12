package core.share.escaped;

/**
 * 객체를 공개:
 * 객체 내부의 private이 아닌 변수나 메서드를 통해 불러올 수 있는 모든 객체(상태)는 함께 공개/유출된다.
 */
public class UnsafeEscapedStatus {

    public static UnsafeInstance status;
    int a;

    static {
        status = new UnsafeInstance(100);
    }

    public UnsafeInstance getStatus() {
        return status;
    }

    static class UnsafeInstance {
        private int value;
        private int iterCount = 10000;

        public UnsafeInstance(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public int getIterCount() {
            return iterCount;
        }

        public void add10000() {
            for (int i = 0; i < iterCount; ++iterCount) {
                value += 1;
            }
        }
    }
}
