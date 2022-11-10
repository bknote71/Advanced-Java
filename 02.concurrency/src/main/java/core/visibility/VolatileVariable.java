package core.visibility;

public class VolatileVariable {

    private static boolean nonVisibleFlag = false;
    private static volatile boolean visibleFlag = false;

    /**
     * 일반적으로 volatile 키워드를 사용해야 가시성이 확보된다.
     * - volatile 키워드와 이전에 접근되는 코드들도 volatile 키워드를 사용하면 꼭 가시성이 확보되는 것이 아닌 모양.
     * - while문 안에 코드가 있어야 가시성이 확보되나봄?
     */
    static class Runnable1 implements Runnable {

        @Override
        public void run() {
            while (!nonVisibleFlag) {
            }
            System.out.println("out1");
        }
    }

    static class Runnable2 implements Runnable {

        @Override
        public void run() {
            while (!visibleFlag) {
            }
            System.out.println("out2");
        }
    }


    public static void main(String[] args) throws InterruptedException {

        new Thread(new Runnable1()).start();
        new Thread(new Runnable2()).start();

        Thread.sleep(1000);

        nonVisibleFlag = true;
        visibleFlag = true;

        // 중요: volatile 변수로 접근하는 코드 이전에 수행된 내용은 volatile 변수에 접근한 이후에서는 모두 가시성이 확보된다.
        // volatile 변수인 visibleFlag 에 접근하는 코드가 실행하게 된다면
        // 그 전에 접근한 non volatile 변수인 nonVisibleFlag 변수의 가시성이 확보된다.

        // 하지만 실제로 해보니 non volatile 변수의 가시성 확보는 확실하지 않다!
    }
}
