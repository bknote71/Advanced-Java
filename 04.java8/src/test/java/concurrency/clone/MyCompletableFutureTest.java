package concurrency.clone;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class MyCompletableFutureTest {

    @Test
    public void complete() throws ExecutionException, InterruptedException {
        final MyCompletableFuture<String> hello = MyCompletableFuture.complete("hello");
        System.out.println(hello.get());
    }

    @Test
    public void asyncSupply() throws ExecutionException, InterruptedException {
        final MyCompletableFuture<String> cf = MyCompletableFuture.supplyAsync(() -> "hello");
        System.out.println(cf.get());

        final MyCompletableFuture<String> cf2 = MyCompletableFuture.supplyAsync(() -> {
            sleep(1000);
            return "hye";
        });

        System.out.println(cf2.get());
    }

    @Test
    public void thenApply() throws ExecutionException, InterruptedException {
        final MyCompletableFuture<String> cf = MyCompletableFuture
                .supplyAsync(() -> "hello")
                .thenApply(s -> {
                    System.out.println("s is: " + s);
                    return s;
                });

        System.out.println(cf.get());
        sleep(100);
    }

    void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}