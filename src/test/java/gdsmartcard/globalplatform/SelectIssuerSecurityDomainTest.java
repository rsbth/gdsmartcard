package gdsmartcard.globalplatform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.security.Security;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

import org.junit.Before;
import org.junit.Test;

import gdsmartcard.AID;
import gdsmartcard.io.GDProvider;

public class SelectIssuerSecurityDomainTest {

    private static final byte TAG_PROPRIETARY_DATA = (byte) 0xA5;
    private static final byte TAG_AID              = (byte) 0x84;
    private static final byte TAG_FCI_TEMPLATE     = (byte) 0x6F;
    GlobalPlatformCardChannel channel;

    @Before
    public void setup() throws Exception {
        Security.addProvider(new GDProvider());
        TerminalFactory factory = TerminalFactory.getInstance("G&D", null);
        CardTerminals terminals = factory.terminals();
        Card card = terminals.list().get(0).connect("*");
        channel = new GlobalPlatformCardChannel(card.getBasicChannel());
    }

    @Test
    public void testDoesTheIssuerSecurityDomainRespondToSelectCommandWithTheFci() throws CardException {
        // ** When the Issuer Security Domain is selected
        ByteBuffer fciBuffer = ByteBuffer.wrap(channel.selectIssuerSecurityDomain().getBytes());

        // ** then it responds with the File Control Information acc. to GlobalPlatform Card Specification 2.1.1, table
        // 9-55
        // the FCI is wrapped in a FCI template
        assertEquals(TAG_FCI_TEMPLATE, fciBuffer.get());
        assertTrue(fciBuffer.get() == fciBuffer.remaining());

        // the AID is contained
        assertEquals(TAG_AID, fciBuffer.get());
        byte[] aidBytes = new byte[fciBuffer.get()];
        fciBuffer.get(aidBytes);
        assertTrue(new AID(aidBytes).isValid());

        // proprietary data is contained
        assertEquals(TAG_PROPRIETARY_DATA, fciBuffer.get());
        assertTrue(fciBuffer.get() == fciBuffer.remaining());
    }

}
