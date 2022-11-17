package lambda.functionalInterface;

import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.*;


public class FunctionTest {

    @Test
    public void function() {
        Integer t = 1;
        Function<Integer, String> func = i -> String.format("num: %d", i);
        assertEquals("num: " + t, func.apply(t));
    }


}