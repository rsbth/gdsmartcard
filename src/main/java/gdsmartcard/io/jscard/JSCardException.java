package gdsmartcard.io.jscard;

import javax.smartcardio.CardException;

import gdsmartcard.io.winscard.WinSCardException;

public class JSCardException extends CardException {

    public JSCardException(WinSCardException cause) {
        super(cause);
    }

    public JSCardException(long errorCode) {
        super(String.format("Error code: %016X", errorCode));
    }

}
