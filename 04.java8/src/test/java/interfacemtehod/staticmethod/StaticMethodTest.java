package interfacemtehod.staticmethod;

import org.junit.Test;

import static org.junit.Assert.*;

public class StaticMethodTest {

    @Test
    public void intSum() {
        assertEquals(3, IntMath.sum(1, 2));
    }

    @Test
    public void intMinus() {
        assertEquals(3, IntMath.minus(6, 3));
    }

    @Test
    public void identity() {
        assertEquals(3, IntMath.identity(3));
    }

}