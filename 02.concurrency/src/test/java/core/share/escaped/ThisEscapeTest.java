package core.share.escaped;

import org.junit.Test;

import static org.junit.Assert.*;

public class ThisEscapeTest {

    @Test
    public void escapedReference() {
        final Client client = new Client();
        final ThisEscape thisEscape = new ThisEscape(client);

        assertEquals(thisEscape.getEscapedReference(), client.getEventListener().getEscapedReference());
    }

    @Test
    public void escapeReferenceByInnerClassSoThread_not_safety() {
        final Client client = new Client();
        final ThisEscape thisEscape = new ThisEscape(client);

        client.add10000();

        assertNotEquals(11111, thisEscape.getEscapedReference().getValue());
        assertEquals(21111, thisEscape.getEscapedReference().getValue());
    }

}