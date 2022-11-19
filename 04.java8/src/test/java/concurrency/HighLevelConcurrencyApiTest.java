package concurrency;

import org.junit.Test;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;

/**
 * High-Level Concurrency API: "Executor"
 * - Executor: 스레드 생성, 스레드 관리, 작업 처리 및 실행
 * <p>
 * ExecutorService
 * - Executor를 상속받은 인터페이스 + 많은 기능이 존재하는 확장된 인터페이스
 * - e.g. Callable 실행 가능(submit) + Executor 종료(shutdown), 여러 Callable 동시에 실행(invoke) 기능
 * - 주로 사용하는 인터페이스이지만 콜백지원은 안되용 ㅜ
 * - 구현체: "ThreadPoolExecutor"
 * <p>
 * ExecutorService/ThreadPoolExecutor
 * - 내부적으로 (스레드 풀 + 블로킹 큐)가 존재
 * - 작업(FutureTask)을 블로킹 큐에 쌓는다.
 * - 스레드 풀에 있는 노는 스레드에 작업 할당
 * <p>
 * ScheduledExecutorService
 * - ExecutorService를 상속받은 인터페이스
 * - delay 이후에 작업 한 번만 실행 or period 주기적으로 작업 실행
 * - 구현체: "ForkJoinPool" (멀티 프로세싱을 사용하는 애플리케이션을 개발할 때 유용한 api?)
 * <p>
 * Executors
 * - ExecutorService or ScheduledExecutorService 인터페이스를 반환하는 static factory method 모음
 * <p>
 * Future
 * - 비동기 작업 결과를 담는 컨테이너
 * - get(): blocking
 * <p>
 * future.cancel(boolean)
 * - true: 인터럽트시키면서 작업 종료 + get()을 통해 값을 가져올 수 없음
 * - false: 작업 끝날때까지 대기 + get()을 통해 값을 가져올 수 없음
 * - get(): 값을 가져오려하면 "CancellationException"
 * - cancel ==> isDone(): true 가 된다.
 * - 작업이 종료되었다는 의미. cancel()했으니 작업 종료는 당연한 이치
 */
public class HighLevelConcurrencyApiTest {

    @Test
    public void execute() throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(2);

        Runnable runnable = () -> {
            System.out.println(Thread.currentThread().getName() + " 입장");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 종료");
        };

        executorService.execute(runnable);
        executorService.execute(runnable);
        executorService.execute(runnable);
        executorService.execute(runnable);

        Thread.sleep(100);
        System.out.println("비동기 실행");

        /**
         * shutdown():
         * Initiates an orderly shutdown in which previously submitted tasks are executed,
         * but no new tasks will be accepted.
         * Invocation has no additional effect if already shut down.
         *
         * This method does not wait for previously submitted tasks to complete execution.
         * Use awaitTermination to do that.
         *
         */

        // 시간이 다 되어도 (인터럽트가 발생X + 작업 종료X) 그냥 통과해버린다.
        executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);

        // 아직 실행중인 작업이 남아있다.
        System.out.println("hh");
        Thread.sleep(1000);
    }

    @Test
    public void submit() throws ExecutionException, InterruptedException {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        // executorService.submit(runnable): Future<?> = null을 담는 컨테이너
        final Future<?> hello = executorService.submit(() -> System.out.println("hello"));
        assertNull(hello.get());

        Callable<String> callable = () -> {
            Thread.sleep(1000);
            return "callable";
        };

        final Future<String> submit = executorService.submit(callable);

        // future.get() 은 블로킹이다.
        // future.isDone() 은 논블로킹
        assertFalse(submit.isDone());
        assertEquals("callable", submit.get()); // 여기서 블로킹
        assertTrue(submit.isDone());

    }

    // executorService: 예외 처리
    // 즉 Callable, Runnable 작업 내부에서 예외가 발생하면 예외도 "결과"로 저장했다가
    // get()로 결과를 꺼낼 때 결과가 "예외"이면 ExecutionException으로 해당 예외를 감싸서 던져준다.
    @Test
    public void invokeAll() throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(3);

        Callable<String> callable1 = () -> "callable1";
        Callable<String> callable2 = () -> "callable2";
        Callable<String> callable3 = () -> "callable3";
        Callable<String> callable4 = () -> {
            System.out.println("callable4 호출");
            if (true) {
                System.out.println("던진다 예외~");
                throw new Exception();
            }
            return "callable4";
        };

        final List<Callable<String>> callables = Arrays.asList(callable1, callable2, callable3, callable4);

        // invokeAll(Collection<? extends Callable<? extends Object>>)
        final List<Future<String>> futures = executorService.invokeAll(callables);

        Thread.sleep(1000);

        for (Future<String> future : futures) {
            try {
                System.out.println("get 호출!");
                System.out.println(future.get());
            } catch (ExecutionException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }

    // executorService의 단점: 1. 콜백이 없음 -> 완전한 비동기라기 보기 어려움 2. 예외 처리 API가 없다. (try catch로 직접..)
    // 작업 도중 예외가 발생하면 바로 예외가 터지는 것이 아니라 결과에 담아두었다가
    // future.get() 하는 순간 해당 예외를 ExecutionException으로 감싸서 던진다.
    // ExecutionException: 쓰레드 실행중에 발생하는 예외 (인터럽트 제외), 체크드 예외
    // 결국 직접 try-catch 문으로 ExecutionException 에 대한 예외 처리를 해야한다는 말씀
    // 작업 도중 해당 스레드에서 발생하는 인터럽트: InterruptedException 이 던져진다.
    // 그럼 해당 인터럽트 예외 역시 결과에 저장되었다가 get() 할 때 ExecutionException으로 감싸져서 외부로 던져진다.
    // 작업 도중 발생하는 인터럽트 vs get() 을 통해 값을 기다릴 때 발생하는 인터럽트
    // 전자는 위에서 말했듯이 ExecutionException으로 감싸져 던져지고 후자는 InterruptedException 자체가 던져진다.
    // 참고로 get()은 호출하는 스레드에서 돌아간다.
    @Test
    public void exceptionAndInterrupt() throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(3);
        final Future<String> submit = executorService.submit(() -> {
            if (true) throw new Exception();
            return "hello";
        });

        try {

            System.out.println(submit.get());

        } catch (InterruptedException e) {
            // get() 으로 꺼내는 도중에 메인 스레드에 인터럽트가 발생하면!!
            System.out.println("인터럽트 발생");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("예외 발생");
            e.printStackTrace();
        }
    }

    @Test
    public void interrupt() throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        final Future<String> submit = executorService.submit(() -> {
            Thread.sleep(2000);
            Thread.currentThread().interrupt();
            return "hello";
        });

        final Thread thread = Thread.currentThread();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread.interrupt();
        }).start();

        try {
            System.out.println(submit.get());

        } catch (InterruptedException e) {
            // get() 으로 꺼내는 도중에 메인 스레드에 인터럽트가 발생하면!!
            System.out.println("인터럽트 발생");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("예외 발생");
            e.printStackTrace();
        }

        System.out.println("?");
        Thread.sleep(100);
    }
    
}
