package concurrency;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.junit.Assert.*;


public class LowLevelConcurrencyApiTest {


    /**
     * Low-level: "Thread" 직접 다루기
     * 수십 수백개의 스레드를 코딩으로 관리하는 것은 매우 힘들고 프로그래머는 그러면 안된다.
     */

    @Test
    public void interrupt() throws InterruptedException {
        final Thread thread = new Thread(new SleepTask());
        thread.start();

        Thread.sleep(1000);
        System.out.println("1초 경과");

        thread.interrupt();
    }

    @Test
    public void join() {
        final Thread thread = new Thread(new SleepTask());
        thread.start();

        // join() 메소드도 "대기" 하는 것이기 때문에 인터럽트가 발생할 수 있다.
        // 즉 InterruptedException이 발생할 수 있다는 뜻이다.
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("thread 안전하게 종료");
    }

    @Test
    public void interruptJoin() throws Exception{
        final Thread thread = new Thread(new SleepTask());
        thread.start();

        final Thread mainThread = Thread.currentThread();

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            mainThread.interrupt();
        }).start();


        try {
            thread.join();
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " 스레드 인터럽트 발생");
            throw new Exception();
        }

        System.out.println("thread 안전하게 종료");
    }

    // FutureTask: (Runnable + Callable) 작업을 감싸는 작업
    // FutureTask -> RunnableFuture -> Future, Runnable
    @Test
    public void futureTask() throws InterruptedException {
        Runnable runnable = () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("runnable");
        };
        Callable callable = () -> "callable";
        final FutureTask runnableTask = new FutureTask(runnable, "runnable");
        final FutureTask callableTask = new FutureTask(callable);

        final Thread runnableThread = new Thread(runnableTask);
        final Thread callableThread = new Thread(callableTask);

        runnableThread.start();
        callableThread.start();
        
        // 작업 결과 받아오기: get = blocking
        try {
            final Object callResult = callableTask.get();
            System.out.println(callResult);

            final Object runResult = runnableTask.get();
            System.out.println(runResult);

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    

}
