package gdsmartcard.io;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;

import gdsmartcard.io.jscard.JSCardContext;
import gdsmartcard.io.jscard.JSCardException;
import gdsmartcard.io.jscard.ReaderGroup;
import gdsmartcard.io.jscard.Scope;

public class PCSCTerminals extends CardTerminals {

    private static final Scope       SCOPE = Scope.USER;
    private static final ReaderGroup GROUP = ReaderGroup.ALL_READERS;
    private JSCardContext            context;

    public PCSCTerminals() {
        try {
            context = JSCardContext.establish(SCOPE);
        } catch (JSCardException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<CardTerminal> list(State state) throws CardException {
        try {
            List<String> readerNames = context.listReaders(GROUP);
            List<CardTerminal> terminals = new Vector<CardTerminal>();
            for (String nextName : readerNames) {
                terminals.add(new PCSCTerminal(context, nextName));
            }
            return Collections.unmodifiableList(terminals);
        } catch (Exception e) {
            throw new CardException(e);
        }

    }

    @Override
    public boolean waitForChange(long timeout) throws CardException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void finalize() throws JSCardException {
        context.release();
    }
}
