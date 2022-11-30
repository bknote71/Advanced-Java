package clone;

import org.junit.Test;

import static org.junit.Assert.*;

public class MyConcurrentLinkedQueueTest {

    @Test
    public void add_item() {
        MyConcurrentLinkedQueue<String> q = new MyConcurrentLinkedQueue<>();
        q.offer("hello");
        q.offer("hi");
        q.offer("aloha");

        System.out.println(q);
    }

    @Test
    public void poll_item() {
        MyConcurrentLinkedQueue<String> q = new MyConcurrentLinkedQueue<>();
        q.offer("hello");
        q.offer("hi");
        q.offer("aloha");

        final String p1 = q.poll();
        final String p2 = q.poll();
        final String p3 = q.poll();

        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);

    }

    @Test
    public void remove_item() {
        MyConcurrentLinkedQueue<String> q = new MyConcurrentLinkedQueue<>();
        q.offer("hello");
        q.offer("hi");
        q.offer("aloha");

        q.remove("hi");

        System.out.println(q);
    }

}