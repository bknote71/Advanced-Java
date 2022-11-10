package reference.weak;

import org.junit.Test;
import reference.Team;

import static org.junit.Assert.*;

public class WeakLinkedListTest {
    @Test
    public void createList() {
        final WeakLinkedList<Team, String> weakLinkedList = new WeakLinkedList<>();

        Team t1 = new Team();
        Team t2 = new Team();
        Team t3 = new Team();

        weakLinkedList.put(t1, "t1");
        weakLinkedList.put(t2, "t2");
        weakLinkedList.put(t3, "t3");
        weakLinkedList.put(t3, "t3");

        weakLinkedList.print();

        assertEquals(weakLinkedList.size(), 3);
    }
    @Test
    public void removeTest() {
        final WeakLinkedList<Team, String> weakLinkedList = new WeakLinkedList<>();

        Team t1 = new Team();
        Team t2 = new Team();
        Team t3 = new Team();

        weakLinkedList.put(t1, "t1");
        weakLinkedList.put(t2, "t2");
        weakLinkedList.put(t3, "t3");

        weakLinkedList.remove(t1);
        weakLinkedList.remove(t2);

        weakLinkedList.print();

        assertEquals(weakLinkedList.size(), 1);
        assertFalse(weakLinkedList.remove(t1));
        assertFalse(weakLinkedList.remove(t2));
    }

    @Test
    public void allocateNullAndNotClean() {
        final WeakLinkedList<Team, String> weakLinkedList = new WeakLinkedList<>();

        Team t1 = new Team();
        Team t2 = new Team();
        Team t3 = new Team();

        weakLinkedList.put(t1, "t1");
        weakLinkedList.put(t2, "t2");
        weakLinkedList.put(t3, "t3");
        weakLinkedList.put(null, "original null");

        assertTrue(weakLinkedList.get(null).equals("original null"));

        t1 = null;
        t3 = null;

        weakLinkedList.print();
        assertEquals(weakLinkedList.size(), 4);

        System.gc();

        assertEquals(weakLinkedList.size(), 4);
    }

    @Test
    public void allocateNullAndClean() throws InterruptedException {
        final WeakLinkedList<Team, String> weakLinkedList = new WeakLinkedList<>();

        Team t1 = new Team();
        Team t2 = new Team();
        Team t3 = new Team();

        weakLinkedList.put(t1, "t1");
        weakLinkedList.put(t2, "t2");
        weakLinkedList.put(t3, "t3");
        weakLinkedList.put(null, "original null");

        assertTrue(weakLinkedList.get(null).equals("original null"));

        t1 = null;
        t3 = null;

        assertEquals(weakLinkedList.size(), 4);

        System.gc();

        Thread.sleep(1000);

        weakLinkedList.clean();
        assertEquals(weakLinkedList.size(), 2);

        weakLinkedList.print();
    }
}