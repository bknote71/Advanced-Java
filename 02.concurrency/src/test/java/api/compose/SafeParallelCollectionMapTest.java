package api.compose;

import org.junit.Test;

import static org.junit.Assert.*;

public class SafeParallelCollectionMapTest {

    @Test
    public void safePut() throws InterruptedException {
        final SafeParallelCollectionMap safeParallelCollectionMap = new SafeParallelCollectionMap();

        new Thread(() -> safeParallelCollectionMap.put(String.class, "abc")).start();
        new Thread(() -> safeParallelCollectionMap.put(String.class, "def")).start();
        new Thread(() -> safeParallelCollectionMap.put(String.class, "ghi")).start();

        Thread.sleep(100);

        assertEquals(1, safeParallelCollectionMap.size());
    }

    @Test
    public void safePutIfAbsent() {
        final SafeParallelCollectionMap safeParallelCollectionMap = new SafeParallelCollectionMap();

        final boolean absent = safeParallelCollectionMap.putIfAbsent(String.class, "String");

        assertTrue(absent);
    }

    @Test
    public void safeIter() throws InterruptedException {
        final SafeParallelCollectionMap safeParallelCollectionMap = new SafeParallelCollectionMap();

        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            safeParallelCollectionMap.put(String.class, "St");

        }).start();

        safeParallelCollectionMap.iter();
    }

}