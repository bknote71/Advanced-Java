package interfacemtehod.defaultmethod;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;


public class CollectionTest {

    @Test
    public void stream() {
        Collection<Integer> collection = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        assertEquals(5, collection.stream().filter(i -> i > 5).count());
    }

    @Test
    public void parallelStream() {
        Collection<Integer> collection = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        collection.parallelStream().forEach(i -> {
            System.out.println("thread: " + Thread.currentThread().getName() + " " + i);
        });
    }

    @Test
    public void removeIf() {
        Collection<Integer> collection = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        // 짝수만 제거
        collection.removeIf(i -> i % 2 == 0);
        System.out.println(collection);
    }
}
