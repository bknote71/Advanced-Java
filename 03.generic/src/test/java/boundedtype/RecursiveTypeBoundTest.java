package boundedtype;

import boundedtype.recursive.Box;
import boundedtype.recursive.IntegerBox;
import boundedtype.recursive.RecursiveTypeBound;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RecursiveTypeBoundTest {

    @Test
    public void listMax() {
        final List<String> hi = List.of("hi", "hello");
        assertEquals("hi", RecursiveTypeBound.max(hi));
    }

    @Test
    public void maxOfIntegerBox() {
        List<IntegerBox> list = new ArrayList<>();
        list.add(new IntegerBox(10, "hi"));
        list.add(new IntegerBox(2, "hello"));

        IntegerBox max = RecursiveTypeBound.max(list);
        System.out.println(max);
        assertEquals(10, max.getValue().intValue());
    }

    @Test
    public void maxOfIntegerBox2() {
        List<Box<Integer>> list = new ArrayList<>();
        list.add(new IntegerBox(10, "hi"));
        list.add(new IntegerBox(2, "hello"));

        final Box<Integer> max = RecursiveTypeBound.max(list);
        System.out.println(max);
        assertEquals(10, max.getValue().intValue());
    }
}