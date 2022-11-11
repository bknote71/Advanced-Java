package core.share.visibility;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class VisibilityTest {

    @Test
    public void synchronizedGet() throws InterruptedException {

        final Visibility visibility = new Visibility();

        final Thread thread = new Thread(() -> {
            while (visibility.getStatus() == 0) {
                // ...
            }
            System.out.println("out1");
        });

        thread.start();
        Thread.sleep(100);

        visibility.setStatus(1);
        thread.join();
    }

    @Test
    public void synchronizedSet() throws InterruptedException {

        final Visibility visibility = new Visibility();

        final Thread thread = new Thread(() -> {
            while (visibility.getHalfStatus() == 0) {
                System.out.printf("...");
            }
            System.out.println("\nout2");
        });

        thread.start();
        Thread.sleep(100);

        visibility.setHalfStatus(1);
        thread.join();
    }

    @Test
    public void volatileGetAndSet() throws InterruptedException {

        final Visibility visibility = new Visibility();

        final Thread thread = new Thread(() -> {
            while (visibility.getVolatileStatus() == 0) {
                // ...
            }
            System.out.println("out3");
        });

        thread.start();
        Thread.sleep(100);

        visibility.setVolatileStatus(1);
        thread.join();
    }

    @Test
    public void volatileFlushTest() throws InterruptedException {
        final Visibility visibility = new Visibility();

        final Thread thread = new Thread(() -> {
            while (visibility.getHalfStatus() == 0) {
                System.out.printf("...");
            }
            System.out.println("\nout4");
        });

        thread.start();
        Thread.sleep(100);

//        visibility.setHalfStatus(1);
        visibility.getVolatileStatus();
//        visibility.setVolatileStatus(1);
        visibility.setHalfStatus(1);
        thread.join();
    }


}
