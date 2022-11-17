package interfacemtehod.defaultmethod;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;

import static org.junit.Assert.*;


public class IterableTest {

    @Test
    public void foreach() {
        Iterable<Integer> iter = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        iter.forEach(System.out::println);
    }

    @Test
    public void spliterator() {
        Iterable<Integer> iter = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        final Spliterator<Integer> spliterator1 = iter.spliterator();
        final Spliterator<Integer> spliterator2 = spliterator1.trySplit();

        spliterator1.tryAdvance(System.out::println);
        spliterator1.tryAdvance(System.out::println);
        spliterator1.tryAdvance(System.out::println);
        spliterator1.tryAdvance(System.out::println);
        spliterator1.tryAdvance(System.out::println);

        System.out.println("=====================================");

        spliterator2.tryAdvance(System.out::println);
        spliterator2.tryAdvance(System.out::println);
        spliterator2.tryAdvance(System.out::println);
        spliterator2.tryAdvance(System.out::println);
        spliterator2.tryAdvance(System.out::println);
    }
}
