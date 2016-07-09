package gdsmartcard.globalplatform;

import javax.smartcardio.CardException;
import javax.smartcardio.ResponseAPDU;

public class ErrorResponseCardException extends CardException {

    private ResponseAPDU response;

    public ErrorResponseCardException(ResponseAPDU response) {
        super(String.format("Response has error SW: 0x%04x", response.getSW()));
        this.response = response;
    }

    public ResponseAPDU getResponseAPDU() {
        return response;
    }
}
