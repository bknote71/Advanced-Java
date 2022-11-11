package core.threadsafe;

import org.junit.Test;

import static org.junit.Assert.*;

public class LazyInitRaceTest {

    @Test
    public void checkSameInstance() throws InterruptedException {

        final LazyInitRace lazyInitRace = new LazyInitRace();

        Object[] objects = new Object[1000];

        for (int i = 0; i < 1000; ++i) {
            int idx = i;
            new Thread(() -> {
                objects[idx] = lazyInitRace.getInstance();
            }).start();
        }

        Thread.sleep(1000);

        // 테스트가 깨질 수 있다.
        assertEquals(1,lazyInitRace.getValue());
    }

}