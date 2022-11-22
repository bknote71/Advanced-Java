package concurrency.clone;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class MyCompletableFuture<T> implements Future<T> {

    /**
     * 스레드 생성, 관리, 작업 실행
     * - 내부적으로 ForkJoinPool 사용
     * <p>
     * 구현할 메서드 목록
     * - supplyAsync()
     * - thenApply()
     * - thenAccept()
     * - thenRun()
     * --> 모두 현재의 CF와 다른 CF 반환
     * - get/join
     * <p>
     * 필드
     * - Object result: 이전 CF에서 넘긴 결과값
     * - Completion stack: result가 채워지면 해야할 작업
     * <p>
     * 작업, Task: Completion
     * - dep: 현재 작업이 끝나고 작업의 결과를 넘길 다음 CF
     * - next: 하나의  CF에서의 n개의 작업을 스택 구조로 표현.
     * - 즉, 다음 CF의 작업이 아니다.
     * - e.g. cf1.작업1(), cf1.작업2(), cf1.작업3(), ... 이면 하나의 cf1에 여러 작업이 스택에 쌓인다.
     * <p>
     * 구현할 Completion: AsyncSupply, UniCompletion (BiCompletion은 나중에)
     * - AsyncSupply: 최초의 작업
     * - UniApply, UniAccept, UniRun()
     * - 모두 내부 콜백 실행
     * <p>
     * 동작 흐름
     * 1. cf1 = CompletableFuture.supplyAsync(Supplier);
     * - supplier 작업을 비동기적으로 실행한다.
     * - 실행이 완료되면 cf1의 result에 결과값을 할당한다. (: dep.result = something)
     * - cf1의 stack이 null이 아니면, 즉 작업이 있으면 작업을 실행한다. (: dep.postComplete())
     * <p>
     * 2. cf2 = cf1.thenApply(Function)
     * - cf1 이전 작업이 완료되면 result에 값이 할당되고 Function이 실행된다.
     * - Function 실행이 완료되면 cf2의 result에 결과값을 채우고 cf2의 작업이 null이 아니면 실행한다.
     * - thenAccept, thenRun 모두 비슷하게 동작한다.
     * <p>
     * 공통
     * get/join:
     * - 이전 cf의 작업이 완료되면 꺼낼 수 있는 result!
     * - 즉 result가 null이 아니면 반환
     * <p>
     * dep.postComplete(): 현재 작업 완료 후 다음 dep(CF) 작업 수행
     * <p>
     * Completion: UniApply, UniAccept, UniRun()
     * - tryFire(int mode): 현재 작업 수행
     * - isLive: UniCompletion -> dep != null ?
     * <p>
     * 최초 작업인 supplyAsync 작업 완료 후 흐름 정리
     * d.postComplete() -> h(this).tryFire() // this = d
     * -> d.postFire(a, mode) -> (d) postComplete() -> h(this).tryFire() cycle
     */

    // result, stack 모두 동시에 접근하기 때문에 cas 알고리즘을 통해 lock-free 하게 접근하도록 한다.
    volatile Object result;
    volatile Completion stack;

    // Async 처리용 스레드 풀: 포크조인풀^^
    private static final Executor ASYNC_POOL = ForkJoinPool.commonPool();

    abstract static class Completion extends ForkJoinTask<Void> implements Runnable {

        // cas 대상이기 때문에 volatile
        volatile Completion next;

        abstract MyCompletableFuture<?> tryFire();

        public final void run() {
            tryFire();
        }

        public final boolean exec() {
            tryFire();
            return false;
        }

        public final Void getRawResult() {
            return null;
        }

        public final void setRawResult(Void v) {
        }
    }

    public static <T> MyCompletableFuture<T> complete(T value) {
        MyCompletableFuture<T> dep = new MyCompletableFuture<>();
        dep.completeValue(value);
        return dep;
    }

    private void completeValue(T ret) {
        RESULT.compareAndSet(this, null, ret);
    }

    // d.postComplete() -> h(this).tryFire() // this = d
    // tryFire()에서 result 채우고 -> d.postFire(a, mode) -> (d) postComplete() -> h(this).tryFire() cycle
    // postComplete: my cf result에 값이 들어왔을 때 실행
    // 이전 cf에서 실행해주는 것: dep.postComplete() 를 호출당함
    private void postComplete() {
        // stack이 비어있으면 안된다.
        Completion s;
        while ((s = stack) == null) {
            System.out.println("stack is null");
        }
        stack.tryFire();
    }

    private MyCompletableFuture<?> postFire(MyCompletableFuture<?> cf) {
        // postComplete() 호출: stack에 있는 작업 실행
        if(stack != null) postComplete();
        return null;
    }

    abstract static class UniCompletion<T, U> extends Completion {
        Executor executor; // 스레드를 생성, 관리, 작업 실행할 Executor (기본은 ForkJoinPool)
        MyCompletableFuture dep;
        MyCompletableFuture src;

        UniCompletion(Executor executor, MyCompletableFuture<U> dep, MyCompletableFuture<T> src) {
            this.executor = executor;
            this.dep = dep;
            this.src = src;
        }

        final boolean isLive() {
            return dep != null;
        }
    }

    // thenApply(Function) 호출 시 생성되는 작업
    static class UniApply<T, U> extends UniCompletion<T, U> {
        // T를 소비하는 입장이라서 super??????
        Function<? super T, ? extends U> fn;

        UniApply(Executor executor, MyCompletableFuture<U> dep,
                 MyCompletableFuture<T> src, Function<? super T, ? extends U> fn) {
            super(executor, dep, src);
            this.fn = fn;
        }

        // d.postComplete() -> h(this).tryFire() // this = d
        // tryFire()에서 작업 실행 -> d.postFire(a, mode) -> (d) postComplete() -> h(this).tryFire() cycle
        @Override
        MyCompletableFuture<?> tryFire() {
            MyCompletableFuture<T> a;
            Function<? super T, ? extends U> f;

            a = src; f = fn;
            // 작업 실행
            // 1. 이전 cf의 결과가 필요함: src에서 얻어옴
            T t = (T) a.result;
            dep.completeValue(fn.apply(t));
            
            return dep.postFire(a);
        }
    }

    // thenAccept(Consumer) 호출 시 생성되는 작업
    static class UniAccept<U> extends UniCompletion<U, Void> {
        Supplier<?> sup;

        UniAccept(Executor executor, MyCompletableFuture<Void> dep,
                  MyCompletableFuture<U> src, Supplier<U> sup) {
            super(executor, dep, src);
            this.sup = sup;
        }

        @Override
        MyCompletableFuture<?> tryFire() {
            return null;
        }
    }

    static class UniRun extends UniCompletion<Void, Void> {
        Runnable run;

        UniRun(Executor executor, MyCompletableFuture<Void> dep,
               MyCompletableFuture<Void> src, Runnable run) {
            super(executor, dep, src);
            this.run = run;
        }

        @Override
        MyCompletableFuture<?> tryFire() {
            return null;
        }
    }

    static class AsyncSupply<T> implements Runnable {
        MyCompletableFuture<T> dep;
        Supplier<? extends T> sup;

        public AsyncSupply(MyCompletableFuture<T> dep, Supplier<? extends T> sup) {
            this.dep = dep;
            this.sup = sup;
        }

        @Override
        public void run() {
            // sup 실행 -> dep의 postComplete 호출
            if (dep.result == null) {
                // 예외 처리도 해줘야 한다.
                try {
                    T ret = sup.get();
                    dep.completeValue(ret);
                } catch (Throwable ex) {
                    // 예외또한 기억해야 한다.
                }
            }
            // dep tryFire 호출하게하는 것이 목표
            // 단, dep의 stack이 null이 아닐 때!!
            if (dep.stack != null) dep.postComplete();
        }
    }

    public static <U> MyCompletableFuture<U> supplyAsync(Supplier<U> sup) {
        return asyncSupplyStage(sup, null);
    }

    private static <U> MyCompletableFuture<U> asyncSupplyStage(Supplier<U> sup, Executor es) {
        // null check
        if (sup == null) throw new NullPointerException();
        // dep
        MyCompletableFuture<U> dep = new MyCompletableFuture<>();

        // AsyncSupply 작업은 바로 실행하는 것이지 스택에 쌓는 것이 아니다!
        // 왜냐? static으로 생성하니까 CF 객체 인스턴스가 필요 없어서 스택에 쌓을 필요가 없음
        // 거기다 static method에서는 this를 넘길 수 없다. (STACK.cas 하는데 this..)

        // executor가 필요하다. --> static executor or 넘기는 es
        Executor e = es != null ? es : ASYNC_POOL;
        e.execute(new AsyncSupply<U>(dep, sup));
        return dep;
    }

    private Object waitingGet(boolean interruptible) throws InterruptedException {
        boolean queued = false;
        Object r;
        Completion s;
        while ((r = result) == null) {
            // spin-wait
            Thread.sleep(10);
        }

        if ((r != null || (r = result) != null) && (s = stack) != null)
            postComplete();
        return r;
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        Object r;
        if ((r = result) == null) // wait
            r = waitingGet(false);
        return (T) r;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    // thenApply, thenAccept, thenRun + async?
    public <U> MyCompletableFuture<U> thenApply(Function<T, U> fn) {
        return uniApplyStage(fn, ASYNC_POOL);
    }

    public <U> MyCompletableFuture<U> thenApplyAsync(Function<T, U> fn, Executor es) {
        return uniApplyStage(fn, es);
    }

    private <U> MyCompletableFuture<U> uniApplyStage(Function<T, U> fn, Executor es) {
        // null check
        if (fn == null)
            throw new NullPointerException();
        // result가 null이 아니면? 바로 작업을 수행해야 한다.
        if (result != null)
            return uniApplyNow(es, fn);
        // dep
        MyCompletableFuture dep = new MyCompletableFuture();
        // 일단 next 작업은 없다고 가정
        UniApply task = new UniApply(es, dep, this, fn);
        STACK.compareAndSet(this, null, task);
        if (result != null)
            task.tryFire();
        return dep;
    }

    private <U> MyCompletableFuture<U> uniApplyNow(Executor es, Function<T, U> fn) {

        MyCompletableFuture<U> dep = new MyCompletableFuture<>();
        // 예외 처리
        if (result instanceof Throwable) {
            dep.result = result;
            return dep;
        }

        // fn 수행
        es.execute(new UniApply(es, dep, this, fn));
        return dep;
    }


    private static final VarHandle RESULT;
    private static final VarHandle STACK;

    static {
        try {
            final MethodHandles.Lookup l = MethodHandles.lookup();
            RESULT = l.findVarHandle(MyCompletableFuture.class, "result", Object.class);
            STACK = l.findVarHandle(MyCompletableFuture.class, "stack", Completion.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
