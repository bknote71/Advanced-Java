package interfacemtehod.defaultmethod;

public class Dog implements Animal{
    @Override
    public void cry() {
        System.out.println("월월");
    }

    @Override
    public String getName() {
        return "강아지";
    }
}
