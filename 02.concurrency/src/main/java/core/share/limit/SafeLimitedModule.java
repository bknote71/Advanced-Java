package core.share.limit;


public class SafeLimitedModule {
    // volatile: 가시성과 64비트 동기화, 재배치 문제를 해결한다.
    private volatile int value = -1;

    static boolean notSafe = false;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    static class WriterThread extends Thread {

        SafeLimitedModule safeLimitedModule;

        public WriterThread(SafeLimitedModule safeLimitedModule) {
            this.safeLimitedModule = safeLimitedModule;
        }

        @Override
        public void run() {
            for (int i = 0, a, b; i < 10000; ++i) {
                a = safeLimitedModule.getValue();

                safeLimitedModule.setValue(i);

                b = safeLimitedModule.getValue();

                notSafe = (a != b - 1 ? true : notSafe);
            }
        }
    }

    static class ReaderThread extends Thread {

        SafeLimitedModule safeLimitedModule;

        public ReaderThread(SafeLimitedModule safeLimitedModule) {
            this.safeLimitedModule = safeLimitedModule;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; ++i) {
                int v = safeLimitedModule.getValue();
                // using v...
            }
        }
    }
}
