package generic;

public class Generics<T> {

    public Generics() {}

    public <T> Generics(T t) {
        System.out.println(t.getClass());
    }

    public static <S> void print(S s) {
        System.out.println("print type: " + s.getClass());
    }

    public <S, T> void print2(S s) {
        System.out.println("print2 type: " + s.getClass());
    }

    public static void main(String[] args) {
        Generics<Number> generics1 = new Generics<>();
        Generics<Number> generics2 = new Generics("hello");

        Generics.print("");
        Generics.print(Integer.valueOf(1));

        generics1.print2("");
        generics1.print2(Integer.valueOf(1));
    }
}
