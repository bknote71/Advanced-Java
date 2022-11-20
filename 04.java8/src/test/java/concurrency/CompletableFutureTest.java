package concurrency;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CompletableFutureTest {
    /**
     * 기존 Future의 단점:
     * 1. 비동기하게 Callback을 호출할 수 없다.
     * - Executor는 콜백을 지원하지 않아요 ㅜㅜ
     * - only get()
     * <p>
     * 2. 예외 처리용 api를 제공하지 않는다.
     * 3. 여러 Future를 조합할 수 없다.
     * 4. 외부에서 완성시킬 수 없다.
     * <p>
     * CompletableFuture
     * - 비동기 프로그래밍을 가능하게 한다. why? Callback 지원
     * - 메서드 체이닝을 통해 "여러 Future와 Callback"을 조합할 수 있다.
     * - 명시적으로 Executor를 만들 필요가 없다.
     * - 외부에서 완성시킬 수 있다.
     * - get(): checked exception + get value
     * - join: unchecked exception + get value
     * - get, join 모두 블로킹
     */

    @Test
    public void complete() throws ExecutionException, InterruptedException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        future.complete("hello~");
        assertEquals("hello~", future.get());
    }

    @Test
    public void callback() throws InterruptedException, ExecutionException {
        // callback = 완전한 비동기^^
        final CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            return "hello";
        });
        final CompletableFuture<Void> vcf1
                = cf.thenApply(s -> s + "~")
                .thenAccept(s -> {
                    System.out.println(Thread.currentThread().getName());
                });

        final CompletableFuture<Void> vcf2
                = cf.thenAccept(System.out::println)
                .thenRun(() -> {
                    System.out.println(Thread.currentThread().getName());
                });

        Thread.sleep(100);
        // thenAccept, thenRun: CF<Void> 반환 -> 결과값 = null 값
        assertNull(vcf1.get());
        assertNull(vcf2.get());
    }

    @Test
    public void async() {
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        CompletableFuture
                .supplyAsync(() -> {
                    System.out.println(Thread.currentThread().getName());
                    return "hello";
                }, executorService)
                .thenAcceptAsync(s -> {
                    System.out.println(Thread.currentThread().getName());
                }, executorService)
                .thenRunAsync(() -> {
                    System.out.println(Thread.currentThread().getName());
                    assertTrue(Thread.currentThread().getName().startsWith("ForkJoinPool"));
                }) // async 에서 Executor를 지정하지 않으면 ForkJoinPool 사용
        ;
    }

    // cf1.thenCombine(cf2, BiFunction): 연관관계가 없는 작업을 따로 비동기적으로 실행
    // 두 개의 관계가 없는 비동기적 작업의 결과를 받아서 조합한다.
    @Test
    public void combine() {
        final CompletableFuture<String> cf1 = CompletableFuture
                .supplyAsync(() -> {
                    sleep(1000);
                    System.out.println(Thread.currentThread().getName());
                    return "hello";
                });

        final CompletableFuture<String> cf2 = CompletableFuture
                .supplyAsync(() -> {
                    sleep(1000);
                    System.out.println(Thread.currentThread().getName());
                    return "aloha";
                });

        // cf1.thenCombine(cf2, BiFunction): 연관관계가 없는 작업을 따로 비동기적으로 실행
        // 두 개의 관계가 없는 비동기적 작업의 결과를 받아서 조합한다.
        final CompletableFuture<String> cf3 = cf1
                .thenCombine(cf2, (s1, s2) -> s1 + " " + s2);

        cf3
                .thenAccept(System.out::println);
    }

    // cf1.thenCompose(cf1결과 -> CF): 연관관계가 있는 작업을 연달아서 처리
    // - cf1 작업 이후 결과를 다음 CF를 생성하는데 이용할 수 있다.
    @Test
    public void compose() {
        final CompletableFuture<String> cf1 = CompletableFuture
                .supplyAsync(() -> {
                    sleep(1000);
                    System.out.println(Thread.currentThread().getName());
                    return "hello";
                });

        // cf1 결과 s -> 다음 CF인 cf2 를 생성하는데 이용할 수 있다.
        // 즉 연관관계가 있는 작업을 정의할 수 있다.
        final CompletableFuture<Integer> cf2 = cf1
                .thenCompose(s -> CompletableFuture.completedFuture(s.length()));

        cf2
                .thenAccept(System.out::println);

    }

    // allOf: 모든 cf 결과를 합쳐서 실행
    // 단점: 결과값이 무의미하다.
    // - allOf()는 CF<Void> 이다.
    // 왜냐하면 결과값의 타입이 같다는 보장도 없고, CF 실행중에 에러가 발생하게 되면?
    @Test
    public void allOf() throws ExecutionException, InterruptedException {
        final CompletableFuture<String> cf1 = CompletableFuture.completedFuture("hello");
        final CompletableFuture<String> cf2 = CompletableFuture.completedFuture("hi");
        final CompletableFuture<String> cf3 = CompletableFuture.completedFuture("aloha");
        final CompletableFuture<Integer> cf4 = CompletableFuture.completedFuture(10);

        // allOf 는 CF<?> 가변인수를 매개변수로 받는다.
        // 즉 모든 타입파라미터의 CF 가 들어갈 수 있다. --> 타입 안정성이 없다.
        final CompletableFuture<Void> vcf = CompletableFuture.allOf(cf1, cf2, cf3, cf4);

        // allOf 제대로 사용하기
        // 즉 모든 작업물의 결과값을 제대로 컬렉션으로 받으려면 어떻게 해야할까?
        final List<CompletableFuture<String>> futures = Arrays.asList(cf1, cf2, cf3);
        final CompletableFuture[] cfs = futures.toArray(new CompletableFuture[futures.size()]);
        final CompletableFuture<List<String>> results = CompletableFuture.allOf(cfs)
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));

        results.get().forEach(System.out::println);
    }

    // anyOf():
    // - 아무 작업 중에 가장 먼저 빨리 끝나는 작업의 결과를 받아서 처리
    // 즉 어떤 결과가 올지 모르는 상태.
    @Test
    public void anyOf() {
        final CompletableFuture<String> cf1 = CompletableFuture.completedFuture("hello");
        final CompletableFuture<String> cf2 = CompletableFuture.completedFuture("hi");
        final CompletableFuture<String> cf3 = CompletableFuture.completedFuture("aloha");

        // anyOf() 역시 CF<?> 가벼인수를 매개변수로 받는다.
        // 즉 아무 타입 파라미터의 CF가 매개변수로 들어갈 수 있다는 말 -> 타입 안정성을 해침
        // anyOf()는 CF<Object>를 반환: 람다 인자의 타입을 추정하기 힘들다.
        // 명시적 타입 선언: 메서드가 사용하는 전용 타입 파라미터만 선언 가능
        CompletableFuture.anyOf(cf1, cf2, cf3)
                .<String>thenApply(s -> s + "1") // thenApply 메소드는 U 타입 파라미터를 전용으로 사용: U 타입 파라미터만 명시 가능
                .thenAccept(System.out::println);
    }

    @Test
    public void exceptionHandling() throws InterruptedException {
        // how to handle exception in CompletableFuture?
        // - 참고로 RuntimeException 만 따로 처리가 가능하다.
        // - 즉 체크 예외는 작업 정의부(람다?)에서 catch 하여 처리해야한다. ㅜㅜ
        // exceptionally: 예외가 발생했을 때만 처리
        // handle: 예외 + 정상적인 결과 모두 처리

        // cf 작업은 단 1번만 실행
        final CompletableFuture<String> cf = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        System.out.println(Thread.currentThread().getName() + " cf");
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (true) throw new RuntimeException("예외에연^^");
                    return "hello";
                });

        // cf 결과를 가지고 exception 처리: cf를 알아서 기다린다. (내부적으로 get or join 호출하겠지? ㅇㅇ)
        final CompletableFuture<Void> cf1 = cf.exceptionally(ex -> {
                    System.out.println(Thread.currentThread().getName() + " cf1");
                    return "exceptionally로 ex 처리";
                })
                .thenAccept(System.out::println);

        // 이거에 따라 cf1, cf2 작업 순서가 달라짐
        // Thread.sleep(1000);

        // cf의 결과값은 언제나 몇번이라도 꺼내쓸 수 있다.
        final CompletableFuture<Void> cf2 = cf.handle((result, ex) -> {
            try {
                System.out.println(Thread.currentThread().getName() + " cf2");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (ex != null) {
                return "handle로 ex 처리";
            }
            return result;
        }).thenAccept(System.out::println);

        // 스레드와 마찬가지로 CF 또한 작업이 완료되기 전에 메인 스레드(CF를 호출하는 스레드)가 종료되면 모든 작업이 종료된다.
        // 따라서 대기할 필요: join/get
        // 중요: CF는 내부적으로 Stack을 사용한다.
        // --> LIFO
        // 여기서는 cf1이 cf2보다 먼저 스택에 들어가면 cf2가 먼저 실행된다.
        // 물론 cf1하고 cf2가 스택에 있을 때
        // cf2작업이 스택에 쌓이기전에 cf1을 처리하려고 poll 하면 cf1이 먼저 실행되는 것
        // 스택에 당연한 이치
        cf1.join();
        cf2.join();



    }

    @Test
    public void composite() throws InterruptedException {
        final ExecutorService es = Executors.newFixedThreadPool(5);

        final CompletableFuture<String> cf1 = CompletableFuture
                .supplyAsync(() -> "hello", es)
                .thenApplyAsync(s -> {
                    try {
                        System.out.println("before: " + Thread.currentThread().getName());
                        Thread.sleep(3000);
                        System.out.println(Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return s;
                }, es);

        final CompletableFuture<Void> cf3 = cf1
                .thenApplyAsync(s -> {
                    try {
                        Thread.sleep(3000);
                        System.out.println(Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return s;
                }, es)
                .thenApply(s -> {
                    try {
                        Thread.sleep(3000);
                        System.out.println(Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return s;
                }).thenAccept(System.out::println);

        Thread.sleep(1000);

        System.out.println(Thread.currentThread().getName());

        cf3.join();
    }

    @Test
    public void composite2() {
        final ExecutorService es = Executors.newFixedThreadPool(5);

        final CompletableFuture<String> cf1 = CompletableFuture
                .supplyAsync(() -> "hello", es)
                .thenApplyAsync(s -> {
                    try {
                        System.out.println("before: " + Thread.currentThread().getName());
                        Thread.sleep(3000);
                        System.out.println("after: " + Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return s;
                }, es);

        final CompletableFuture<Void> cf3 = cf1
                .thenApplyAsync(s -> {
                    try {
                        Thread.sleep(3000);
                        System.out.println(Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return s;
                }, es)
                .thenApply(s -> {
                    try {
                        Thread.sleep(3000);
                        System.out.println(Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return s;
                }).thenAccept(System.out::println);

        final CompletableFuture<Void> cf4 = cf1
                .thenApplyAsync(s -> {
                    try {
                        Thread.sleep(3000);
                        System.out.println(Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return s;
                }, es)
                .thenApply(s -> {
                    try {
                        Thread.sleep(3000);
                        System.out.println(Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return s;
                }).thenAccept(System.out::println);


        final CompletableFuture<Void> cf5 = cf1
                .thenApplyAsync(s -> {
                    try {
                        Thread.sleep(3000);
                        System.out.println(Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return s;
                }, es)
                .thenApply(s -> {
                    try {
                        Thread.sleep(3000);
                        System.out.println(Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return s;
                }).thenAccept(System.out::println);


        sleep(100);
        System.out.println("왜 안찍혀;;");
        System.out.println(Thread.currentThread().getName());

        cf3.join();
        cf4.join();
        cf5.join();
    }


    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
