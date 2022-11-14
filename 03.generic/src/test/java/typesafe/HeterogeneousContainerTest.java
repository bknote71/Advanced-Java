package typesafe;

import org.junit.Test;

import static org.junit.Assert.*;

public class HeterogeneousContainerTest {

    @Test
    public void putAndGet() {
        final HeterogeneousContainer heterogeneousContainer = new HeterogeneousContainer();
        heterogeneousContainer.put1(String.class, "abc");
        heterogeneousContainer.put1(Integer.class, 123);
        heterogeneousContainer.put1(String.class, "cde");

        String str = heterogeneousContainer.get(String.class);
        Integer integer = heterogeneousContainer.get(Integer.class);

        assertEquals("cde", str);
        assertEquals(123, integer.intValue());
    }

    @Test
    public void wrongValueByPut1() {
        final HeterogeneousContainer heterogeneousContainer = new HeterogeneousContainer();
        heterogeneousContainer.put1((Class) String.class, 123);

        assertThrows(ClassCastException.class, () -> {
            String s = heterogeneousContainer.get(String.class);
        });
    }

    @Test
    public void wrongValueByPut2() {
        final HeterogeneousContainer heterogeneousContainer = new HeterogeneousContainer();

        assertThrows(ClassCastException.class, () -> {
            heterogeneousContainer.put2((Class) String.class, 123);
        });
    }

}