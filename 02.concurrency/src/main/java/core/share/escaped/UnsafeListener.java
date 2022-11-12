package core.share.escaped;

public class UnsafeListener {

    private int value;

    public UnsafeListener() {
        value = 1;

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> value += 1).start();
    }

    public UnsafeListener(int iter) {
        final Thread thread = new Thread(() -> {
            for (int i = 0; i < iter; ++i) {
                value += 1;
            }
        });

        thread.start();

        for (int i = 0; i < iter; ++i) {
            value += 1;
        }

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getValue() {
        return value;
    }
}
