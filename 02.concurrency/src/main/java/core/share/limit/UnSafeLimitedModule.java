package core.share.limit;

public class UnSafeLimitedModule {

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

        UnSafeLimitedModule unSafeLimitedModule;

        public WriterThread(UnSafeLimitedModule unSafeLimitedModule) {
            this.unSafeLimitedModule = unSafeLimitedModule;
        }

        @Override
        public void run() {
            for (int i = 0, a, b; i < 10000; ++i) {
                unSafeLimitedModule.setValue(i);
            }
        }
    }

    static class ReadAndCompareThread extends Thread {

        UnSafeLimitedModule unSafeLimitedModule;

        public ReadAndCompareThread(UnSafeLimitedModule unSafeLimitedModule) {
            this.unSafeLimitedModule = unSafeLimitedModule;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; ++i) {
                int value = unSafeLimitedModule.getValue();
                notSafe = (value == unSafeLimitedModule.getValue() ? true : notSafe);
            }
        }
    }
}
