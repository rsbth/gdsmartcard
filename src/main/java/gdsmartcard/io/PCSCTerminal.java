package gdsmartcard.io;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

import gdsmartcard.io.jscard.JSCard;
import gdsmartcard.io.jscard.JSCardContext;
import gdsmartcard.io.jscard.JSCardException;
import gdsmartcard.io.jscard.Protocol;
import gdsmartcard.io.jscard.ShareMode;
import gdsmartcard.io.winscard.WinSCardDefines;
import gdsmartcard.io.winscard.WinSCardReaderState;

public class PCSCTerminal extends CardTerminal {

    private static final ShareMode SHARE_MODE = ShareMode.SHARED;
    private String                 name;
    private JSCardContext          context;
    private JSCard                 card;

    public PCSCTerminal(JSCardContext context, String name) {
        this.context = context;
        this.name = name;
    }

    @Override
    public Card connect(String protocol) throws CardException {
        Protocol p;
        switch (protocol) {
        case "T=0":
            p = Protocol.T0;
            break;
        case "T=1":
            p = Protocol.T1;
            break;
        case "*":
            p = Protocol.ANY;
            break;
        default:
            throw new CardException("Unsupported protocol: " + protocol);
        }
        try {
            card = context.connect(getName(), SHARE_MODE, p);
            return new PCSCCard(card);
        } catch (JSCardException e) {
            throw new CardException(e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isCardPresent() throws CardException {
        WinSCardReaderState[] status;
        try {
            status = context.getStatusChange(100, getName());
        } catch (JSCardException e) {
            throw new CardException(e);
        }
        System.out.println(status[0].toString());
        return (status[0].dwEventState.intValue() & WinSCardDefines.SCARD_STATE_PRESENT) != 0;
    }

    @Override
    public boolean waitForCardAbsent(long arg0) throws CardException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean waitForCardPresent(long arg0) throws CardException {
        // TODO Auto-generated method stub
        return false;
    }

}
