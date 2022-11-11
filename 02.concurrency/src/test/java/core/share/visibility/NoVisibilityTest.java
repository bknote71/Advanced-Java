package core.share.visibility;

import org.junit.Test;

import static org.junit.Assert.*;

public class NoVisibilityTest {

    @Test
    public void noVisibility() throws InterruptedException {

        final NoVisibility noVisibility = new NoVisibility();

        final Thread thread = new Thread(() -> {
            while (noVisibility.getStatus() == 0) {
                // ...
            }
            System.out.println("out status: " + noVisibility.getStatus());
        });

        thread.start();
        Thread.sleep(100);

        noVisibility.setStatus(1);
        thread.join();
    }

}