package lambda.methodReference;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class Greeting {

    int value;
    static int staticValue = 10;


    public Greeting() {

    }

    public Greeting(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static int getStaticValue() {
        return staticValue;
    }

    public static void setStaticValue(int x) {
        staticValue = x;
    }

    // 내림차순 정렬
    public int myComp(Greeting greeting) {
        return greeting.value - this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
