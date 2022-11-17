package interfacemtehod.defaultmethod;

public interface Animal {
    void cry();

    String getName();

    // 기본: public, default -> abstract/static X
    default void isCute() {
        System.out.println(getName() + " is sooooo~ cute");
    }

    default void isIntelligentBetterThanPeople() {
        System.out.println(getName() + " is stupid");
    }

    default void match(Animal animal) {
        System.out.println(getName() + "는 " + animal.getName() + "와 맺어졌다.");
    }

    default boolean nameIs(String name) {
        return getName().equals(name);
    }
}
