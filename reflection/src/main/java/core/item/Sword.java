package core.item;

import core.job.Warrior;

@Warrior
public class Sword extends Item {
    // 양손검, 한손검
    private int handNumber;

    public Sword(String name, int handNum) {
        super(name);
        this.handNumber = handNum;
    }

    @Override
    public void attack() {
        System.out.println("슥");
    }
}
