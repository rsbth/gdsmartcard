package gdsmartcard.io;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

/**
 * The set of terminals supported by the '<code>G&D</code>'
 * {@link TerminalFactory}. This set includes all terminals connected via the
 * PC/SC interface and the CT-API as well as a virtual terminal connecting to
 * the Infineon simulator socket interface and the G&D CCR5xx card readers
 * connected to any COM port.
 */
public class GDCardTerminals extends CardTerminals {

    private PCSCTerminals pcscTerminals = new PCSCTerminals();
    private CTAPITerminals ctapiTerminals = new CTAPITerminals();
    private CCR5xxTerminals ccr5xxTerminals = new CCR5xxTerminals();
    private InfineonSimulatorTerminals ifxTerminals = new InfineonSimulatorTerminals();

    @Override
    public List<CardTerminal> list(State state) throws CardException {
        List<CardTerminal> allTerminals = new Vector<CardTerminal>();

        allTerminals.addAll(pcscTerminals.list(state));
        allTerminals.addAll(ctapiTerminals.list(state));
        allTerminals.addAll(ccr5xxTerminals.list(state));
        allTerminals.addAll(ifxTerminals.list(state));

        return Collections.unmodifiableList(allTerminals);
    }

    @Override
    public boolean waitForChange(long timeout) throws CardException {
        // TODO Auto-generated method stub
        return false;
    }

}
