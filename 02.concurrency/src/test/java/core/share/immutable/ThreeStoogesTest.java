package core.share.immutable;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ThreeStoogesTest {

    @Test
    public void contain_test() {
        final ThreeStooges threeStooges = new ThreeStooges();

        assertTrue(threeStooges.isStooge("ABC"));
        assertTrue(threeStooges.isStooge("DEF"));
        assertTrue(threeStooges.isStooge("GHI"));
    }

    @Test
    public void immutable_reference() {
        final ThreeStooges threeStooges = new ThreeStooges();

        Set<String> stooges0 = threeStooges.getStooges();
        Set<String> stooges1 = threeStooges.getStooges();
        Set<String> stooges2 = threeStooges.getStooges();

        stooges1 = new HashSet<>();

        assertEquals(stooges0, stooges2);
    }

    @Test
    public void mutable_set_state1() {
        final ThreeStooges threeStooges = new ThreeStooges();

        assertFalse(threeStooges.isStooge("me"));

        threeStooges.put("me");

        assertTrue(threeStooges.isStooge("me"));
    }

    @Test
    public void mutable_set_state2() {
        final ThreeStooges threeStooges = new ThreeStooges();

        assertFalse(threeStooges.isStooge("me"));

        Set<String> stooges = threeStooges.getStooges();
        stooges.add("me");

        assertTrue(threeStooges.isStooge("me"));
    }

    @Test
    public void accessFiledByReflection() throws NoSuchFieldException, IllegalAccessException {
        final ThreeStooges threeStooges = new ThreeStooges();
        final Class<ThreeStooges> threeStoogesClass = ThreeStooges.class;

        // final Field[] declaredFields = threeStoogesClass.getDeclaredFields();
        final Field stooges = threeStoogesClass.getDeclaredField("stooges");
        stooges.setAccessible(true);

        Set set = (Set) stooges.get(threeStooges);

        assertTrue(set.contains("ABC"));
        assertTrue(set.contains("DEF"));
        assertTrue(set.contains("GHI"));
        assertFalse(set.contains("me"));

        set.add("me");

        assertTrue(set.contains("me"));

    }
}