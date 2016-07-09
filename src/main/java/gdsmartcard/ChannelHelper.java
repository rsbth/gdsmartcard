package gdsmartcard;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

public class ChannelHelper {

    private CardChannel channel;

    public ChannelHelper(CardChannel channel) {
        this.channel = channel;
    }

    public byte[] transmit(CommandAPDU command) throws CardException {
        return transmit(command, 0x9000);
    }

    public byte[] transmit(CommandAPDU command, int expectedSW) throws CardException {
        ResponseAPDU response = channel.transmit(command);
        if (response.getSW() != expectedSW) {
            throw new CardException(String.format("Unexpected SW: 0x%04X", response.getSW()));
        }
        return response.getData();
    }

}
