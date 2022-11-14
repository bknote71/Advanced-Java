package boundedtype;

public class MyClass<T extends Number> {
    private T object;

    public MyClass() {

    }
    public MyClass(T t) {
        this.object = t;
    }

    public void printType() {
        System.out.println(object.getClass());
    }

    public byte byteValue() {
        return object.byteValue();
    }

    public int intValue() {
        return object.intValue();
    }

    public double doubleValue() {
        return object.doubleValue();
    }
}
