package core.item;

import core.job.Common;

import java.util.concurrent.ThreadLocalRandom;

@Common
public class Item {

    private static Long ITEM_ID = 0L;
    private final Long id;
    private String name;
    private int starforce = 0;

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    Item() {
        this.id = ITEM_ID++;
    }

    public Item(String name) {
        this.id = ITEM_ID++;
        this.name = name;
    }

    public void strengthen(int star) {
        System.out.println("뚝딱뚝딱..");
        final int randomValue = random.nextInt();

        sleep(500);

        if (randomValue % 2 == 0) {
            System.out.println("스타포스 강화 성공");
            starforce = star + 1;
        } else System.out.println("스타포스 강화 실패!!");
    }

    public void attack() {}

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStarforce() {
        return starforce;
    }
}
