package gdsmartcard.io;

import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

public interface CommunicationListener {

    void transmitting(CommandAPDU command);

    void received(ResponseAPDU response);

}
