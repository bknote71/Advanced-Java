package clone;

import org.junit.Test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.SynchronousQueue;

import static org.junit.Assert.*;

public class MyFutureTaskTest {

    @Test
    public void call() throws InterruptedException {
        final MyFutureTask<String> task = new MyFutureTask<>(() -> {
            System.out.println("Hhhhhhhhhh");
            return "hello";
        });

        new Thread(task).start();

        Thread.sleep(100);
    }

    @Test
    public void get() throws InterruptedException, ExecutionException {
        final MyFutureTask<String> task = new MyFutureTask<>(() -> {
            System.out.println("Hhhhhhhhhh");
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName());
            return "hello";
        });
        new Thread(task).start();

        final String s = task.get(); // blocking
        System.out.println(s);
    }

    @Test
    public void multiThreadWrite() throws InterruptedException, ExecutionException {
        final MyFutureTask<String> task = new MyFutureTask<>(() -> {
            System.out.println("Hhhhhhhhhh");
            Thread.sleep(3000);
            System.out.println(Thread.currentThread().getName());
            return "hello";
        });

        final Thread thread1 = new Thread(task);
        final Thread thread2 = new Thread(task);
        final Thread thread3 = new Thread(task);
        final Thread thread4 = new Thread(task);
        final Thread thread5 = new Thread(task);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();

        Thread.sleep(1000);

        int sum = 0;
        sum += btoi(thread1.isAlive());
        sum += btoi(thread2.isAlive());
        sum += btoi(thread3.isAlive());
        sum += btoi(thread4.isAlive());
        sum += btoi(thread5.isAlive());

        assertEquals(1, sum);

        System.out.println(task.get());
    }

    @Test
    public void multiThreadRead() throws ExecutionException, InterruptedException {
        final MyFutureTask<String> task = new MyFutureTask<>(() -> {
            System.out.println("Hhhhhhhhhh");
            Thread.sleep(3000);
            System.out.println(Thread.currentThread().getName());
            return "hello";
        });

        final Thread thread0 = new Thread(task);

        final Thread thread1 = new Thread(() -> getto(task));
        final Thread thread2 = new Thread(() -> getto(task));
        final Thread thread3 = new Thread(() -> getto(task));
        final Thread thread4 = new Thread(() -> getto(task));
        final Thread thread5 = new Thread(() -> getto(task));

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();

        int sum = 0;
        sum += btoi(thread1.isAlive());
        sum += btoi(thread2.isAlive());
        sum += btoi(thread3.isAlive());
        sum += btoi(thread4.isAlive());
        sum += btoi(thread5.isAlive());

        assertEquals(5, sum);

        System.out.println(task.get());
    }

    private void getto(MyFutureTask<String> task) {
        try {
            task.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    int btoi(boolean b) {
        return Boolean.compare(b, false);
    }
}