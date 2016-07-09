package gdsmartcard.io;

import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactorySpi;

/**
 * Implementation of the {@link TerminalFactorySpi} that engines the
 * {@link GDCardTerminals}.
 */
public class GDTerminalFactorySpi extends TerminalFactorySpi {

    public GDTerminalFactorySpi(Object param) {
    }

    @Override
    protected CardTerminals engineTerminals() {
        return new GDCardTerminals();
    }

}
