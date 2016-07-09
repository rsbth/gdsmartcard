package gdsmartcard.io;

import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

import org.junit.Assert;
import org.junit.Test;

public class TestProvider {

    @Test
    public void test() throws NoSuchAlgorithmException, CardException {
        Security.addProvider(new GDProvider());
        TerminalFactory terminalFactory = TerminalFactory.getInstance("G&D", null);

        CardTerminals terminals = terminalFactory.terminals();
        Assert.assertTrue(terminals != null);

        for (CardTerminal t : terminals.list()) {
            System.out.println(t.getName());
            Assert.assertTrue(t.isCardPresent());
            Card card = t.connect("*");
            Assert.assertTrue(t.isCardPresent());
            Assert.assertNotNull(card);
        }
    }

}
