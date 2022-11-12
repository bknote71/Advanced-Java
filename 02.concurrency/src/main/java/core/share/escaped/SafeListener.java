package core.share.escaped;

public class SafeListener {

    private Thread thread;
    private int value;

    private SafeListener() {
        thread = new Thread(() -> {
            for (int i = 0; i < 10000; ++i) {
                value += 1;
            }
        });

        for (int i = 0; i < 10000; ++i) {
            value += 1;
        }
    }

    public int getValue() {
        return value;
    }

    public static SafeListener newInstance() throws InterruptedException {
        SafeListener safeListener = new SafeListener();
        safeListener.thread.start();
        safeListener.thread.join();
        return safeListener;
    }
}
