package interfacemtehod.defaultmethod;

public class Cat implements Animal{
    @Override
    public void cry() {
        System.out.println("야옹");
    }

    @Override
    public String getName() {
        return "고양이";
    }
}
