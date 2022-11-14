package boundedtype.recursive;

public class Box<T extends Comparable<T>> implements Comparable<Box<T>> {

    protected final T value;

    public Box(T value) {
        this.value = value;
    }

    @Override
    public int compareTo(Box o) {
        return this.value.compareTo((T)o.value);
    }

    @Override
    public String toString() {
        return "Box{" +
                "value = " + value +
                "}";
    }

    public T getValue() {
        return value;
    }
}
