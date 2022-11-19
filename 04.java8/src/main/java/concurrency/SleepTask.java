package concurrency;

public class SleepTask implements Runnable{

    /**
     * java interrupt
     * - Interrupt 발생: 스레드에서 InterruptedException이 발생한다.
     * - catch 하여 다음 처리를 할 수 있다.
     * - 인터럽트 발생시키기: thread.interrupt() -> 해당 스레드에서 예외 발생
     */

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // 인터럽트가 발생하면 다음 처리가 가능.
            System.out.println("interrupt occurred");
        }
    }
}
