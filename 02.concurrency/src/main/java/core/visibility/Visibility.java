package core.visibility;

public class Visibility {
    public boolean flag = false;

    /**
     * synchronized 블럭을 사용할 경우 블럭을 벗어날 때, 프로세서에 있는 아직 flush 되지 않은 데이터들을 모두 메인 메모리에 반영한다.
     * 그리고 synchronized 블럭에 접근할 때에는 메인 메모리로부터 데이터를 가져온다.
     * --> 즉 synchronized 블럭을 사용하면 가시성이 확보된다.
     *
     * 근데 실제로 해보니, 값을 설정할 때 synchronized 블럭을 사용하지 않아도 메인 메모리에 flush가 되나본데?
     * - 값을 가져올 때는 synchronized 블럭을 사용해야만 메인 메모리에서 값을 가져오는 듯 하다.
     * - 일반적으로 synchronized 블럭을 사용해서 값을 가져올 때 가시성이 확보된다고 생각하자.
     * - 이상하게 synchronized 블럭을 사용하지 않아도 while문 안에 뭔가를 추가하면 가시성 확보가 되는데, 이게 뭐지??
     */
    public boolean isFlagNotSynchronized() {
        return flag;
    }

    public synchronized boolean isFlagSynchronized() {
        return flag;
    }

    public void setFlagNotSynchronized(boolean flag) {
        this.flag = flag;
    }

    public synchronized void setFlagSynchronized(boolean flag) {
        this.flag = flag;
    }

    public static void main(String[] args) throws InterruptedException {
        final Visibility visibility = new Visibility();

        new Thread(() -> {
            while (!visibility.isFlagNotSynchronized()) {
            }
            System.out.println(Thread.currentThread().getName() + " out1");
        }).start();

        new Thread(() -> {
            while (!visibility.isFlagSynchronized()) {
            }
            System.out.println(Thread.currentThread().getName() + " out2");
        }).start();

        Thread.sleep(1000);
        visibility.setFlagNotSynchronized(true);
//        visibility.setFlagSynchronized(true);
    }
}
