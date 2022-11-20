package concurrency.clone;

public class MyCompletableFuture {

    /**
     * 스레드 생성, 관리, 작업 실행
     * - 내부적으로 ForkJoinPool 사용
     *
     * 구현할 메서드 목록
     * - supplyAsync()
     * - thenApply()
     * - thenAccept()
     * - thenRun()
     * --> 모두 현재의 CF와 다른 CF 반환
     * - get/join
     *
     * 필드
     * - Object result: 이전 CF에서 넘긴 결과값
     * - Completion stack: result가 채워지면 해야할 작업
     *
     * 작업, Task: Completion
     * - dep: 현재 작업이 끝나고 작업의 결과를 넘길 다음 CF
     * - next: 하나의  CF에서의 n개의 작업을 스택 구조로 표현.
     * - 즉, 다음 CF의 작업이 아니다.
     * - e.g. cf1.작업1(), cf1.작업2(), cf1.작업3(), ... 이면 하나의 cf1에 여러 작업이 스택에 쌓인다.
     *
     * 구현할 Completion: AsyncSupply, UniCompletion (BiCompletion은 나중에)
     * - AsyncSupply: 최초의 작업
     * - UniApply, UniAccept, UniRun()
     * - 모두 내부 콜백 실행
     * 
     * 동작 흐름
     * 1. cf1 = CompletableFuture.supplyAsync(Supplier);
     * - supplier 작업을 비동기적으로 실행한다.
     * - 실행이 완료되면 cf1의 result에 결과값을 할당한다. (: dep.result = something)
     * - cf1의 stack이 null이 아니면, 즉 작업이 있으면 작업을 실행한다. (: dep.postComplete())
     *
     * 2. cf2 = cf1.thenApply(Function)
     * - cf1 이전 작업이 완료되면 result에 값이 할당되고 Function이 실행된다.
     * - Function 실행이 완료되면 cf2의 result에 결과값을 채우고 cf2의 작업이 null이 아니면 실행한다.
     * - thenAccept, thenRun 모두 비슷하게 동작한다.
     *
     * 공통
     * get/join: 
     * - 이전 cf의 작업이 완료되면 꺼낼 수 있는 result!
     * - 즉 result가 null이 아니면 반환
     *
     * dep.postComplete(): 현재 작업 완료 후 다음 dep(CF) 작업 수행
     *
     * Completion: UniApply, UniAccept, UniRun()
     * - tryFire(int mode): 현재 작업 수행 
     * - isLive: UniCompletion -> dep != null ?
     *
     * 최초 작업인 supplyAsync 작업 완료 후 흐름 정리
     * d.postComplete() -> h(this).tryFire() // this = d
     * -> d.postFire(a, mode) -> (d) postComplete() -> h(this).tryFire() cycle
     */

}
