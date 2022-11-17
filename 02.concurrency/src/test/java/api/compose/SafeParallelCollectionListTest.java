package api.compose;

import org.junit.Test;

import static org.junit.Assert.*;

public class SafeParallelCollectionListTest {

    // CopyOnWriteList는 putIfAbsent 를 구현하지 않았다
    // ==> 스레드 불안정
    @Test
    public void unsafePutIfAbsent() throws InterruptedException {
        final SafeParallelCollectionList safeParallelCollectionList = new SafeParallelCollectionList();

        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            safeParallelCollectionList.add(1);
        }).start();

        boolean absent = safeParallelCollectionList.putIfAbsent(1);

        Thread.sleep(1000);
        assertFalse(absent);
        assertNotEquals(1, safeParallelCollectionList.size());
    }

    @Test
    public void iterTest() throws InterruptedException {
        final SafeParallelCollectionList safeParallelCollectionList = new SafeParallelCollectionList();
        safeParallelCollectionList.add(1);
        safeParallelCollectionList.add(2);
        safeParallelCollectionList.add(3);

        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            safeParallelCollectionList.add(4);
        }).start();

        safeParallelCollectionList.iter();
    }

}