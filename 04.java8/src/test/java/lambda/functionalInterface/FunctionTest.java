package lambda.functionalInterface;

import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.*;


public class FunctionTest {

    @Test
    public void function() {
        System.out.println("==============function==============");
        Integer t = 1;
        Function<Integer, String> func = i -> String.format("num: %d", i);
        assertEquals("num: " + t, func.apply(t));
    }

    @Test
    public void compose() {
        System.out.println("==============compose==============");
        Function<Integer, String> func1 = i -> String.format("i%d", i);
        Function<Integer, Integer> plus10 = i -> i + 10;

        System.out.println(func1.compose(plus10).apply(10));
    }

    @Test
    public void andThen() {
        System.out.println("==============andThen==============");
        Function<Integer, String> func1 = i -> String.format("i%d", i);
        Function<String, String> plus10 = i -> i + 10;

        System.out.println(func1.andThen(plus10).apply(10));
    }

}