package interfacemtehod.staticmethod;

public interface IntMath {

    static int sum(int a, int b) {
        return a + b;
    }

    static int minus(int a, int b) {
        return a - b;
    }

    static int identity(int a) {
        return a;
    }
}
