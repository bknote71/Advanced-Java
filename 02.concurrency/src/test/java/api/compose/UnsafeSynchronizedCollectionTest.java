package api.compose;

import org.junit.Test;

import java.util.ConcurrentModificationException;

import static org.junit.Assert.*;

public class UnsafeSynchronizedCollectionTest {

    @Test
    public void unsafePutIfAbsent() throws InterruptedException {
        final UnsafeSynchronizedCollection unsafeSynchronizedCollection = new UnsafeSynchronizedCollection();

        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            unsafeSynchronizedCollection.put(1);
        }).start();

        final boolean absent = unsafeSynchronizedCollection.unsafePutIfAbsent(1);

        Thread.sleep(1000);
        assertFalse(absent);
    }

    @Test
    public void safePutIfAbsent() throws InterruptedException {
        final UnsafeSynchronizedCollection unsafeSynchronizedCollection = new UnsafeSynchronizedCollection();

        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            unsafeSynchronizedCollection.put(1);
        }).start();

        final boolean absent = unsafeSynchronizedCollection.safePutIfAbsent(1);

        Thread.sleep(1000);
        assertTrue(absent);
    }

    @Test
    public void iteratorTest() throws InterruptedException {
        final UnsafeSynchronizedCollection unsafeSynchronizedCollection = new UnsafeSynchronizedCollection();
        unsafeSynchronizedCollection.put(1);
        unsafeSynchronizedCollection.put(2);
        unsafeSynchronizedCollection.put(3);


        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            unsafeSynchronizedCollection.put(4);
        }).start();

        assertThrows(ConcurrentModificationException.class, unsafeSynchronizedCollection::iter);



    }
}