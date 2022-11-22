package core.item;

import core.job.Archer;

@Archer
public class Bow extends Item {

    private int arrowNumber = 0;

    public Bow(String name, int num) {
        super(name);
        this.arrowNumber = num;
    }

    public int getArrowNumber() {
        return arrowNumber;
    }

    @Override
    public void attack() {
        for (int i = 0; i < arrowNumber; ++i) {
            System.out.println("푸슉!");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
