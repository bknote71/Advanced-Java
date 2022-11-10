package core.visibility;

public class NonVisibility {
    public static boolean flag = false;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            while (!flag) {

            }
            System.out.println("out!");
        }).start();

        Thread.sleep(1000);
        flag = true;
    }
}
