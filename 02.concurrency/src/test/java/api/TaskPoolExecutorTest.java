package api;

import org.junit.Test;

import java.util.concurrent.*;

public class TaskPoolExecutorTest {

    @Test
    public void use() throws InterruptedException {
        final ExecutorService es = Executors.newFixedThreadPool(3);

        final Future<Integer> hello1 = es.submit(() -> {
            System.out.println("hello1");
            return 1;
        });

        final Future<Integer> hello2 = es.submit(() -> {
            System.out.println("hello2");
            return 2;
        });

        final Future<Integer> hello3 = es.submit(() -> {
            System.out.println("hello3");
            return 3;
        });

        Thread.sleep(100);
    }

    @Test
    public void whenWorkerQueueOffer() throws InterruptedException {
        final ExecutorService es = Executors.newFixedThreadPool(1);
        final ThreadPoolExecutor tpe = (ThreadPoolExecutor) es;
        tpe.setKeepAliveTime(1, TimeUnit.MICROSECONDS);
        tpe.allowCoreThreadTimeOut(true);

        es.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("h");
        });

        Thread.sleep(2000);

        Runnable runnable = () -> System.out.println("hello");

        es.submit(runnable);

        System.out.println("end");
        Thread.sleep(3000);
    }
}
