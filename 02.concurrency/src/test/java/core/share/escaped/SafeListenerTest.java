package core.share.escaped;

import org.junit.Test;

import static org.junit.Assert.*;


public class SafeListenerTest {

    @Test
    public void safeConstructor() throws InterruptedException {
        final SafeListener safeListener = SafeListener.newInstance();

        assertEquals(20000, safeListener.getValue());
    }
}
