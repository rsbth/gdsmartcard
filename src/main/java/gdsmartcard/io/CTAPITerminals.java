package gdsmartcard.io;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;

public class CTAPITerminals extends CardTerminals {

    @Override
    public List<CardTerminal> list(State state) throws CardException {
        return Collections.unmodifiableList(new Vector<CardTerminal>());
    }

    @Override
    public boolean waitForChange(long timeout) throws CardException {
        // TODO Auto-generated method stub
        return false;
    }

}
