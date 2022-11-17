package lambda;

import org.junit.Test;

import static org.junit.Assert.*;

public class ShadowingVariableTest {


    @Test
    public void cantShadowingByLambda() {
        final ShadowingVariable shadowingVariable = new ShadowingVariable();
        assertEquals(20, shadowingVariable.plus10baseNumberByLambda(10));
    }

    @Test
    public void canShadowingByLocal() {
        final ShadowingVariable shadowingVariable = new ShadowingVariable();
        assertNotEquals(20, shadowingVariable.plus10baseNumberByLocal(10));
    }

}