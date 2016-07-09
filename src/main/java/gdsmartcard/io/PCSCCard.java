package gdsmartcard.io;

import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;

import gdsmartcard.io.jscard.Disposition;
import gdsmartcard.io.jscard.JSCard;
import gdsmartcard.io.jscard.JSCardException;

public class PCSCCard extends Card {

    private JSCard          card;
    private PCSCCardChannel basicChannel;

    public PCSCCard(JSCard card) {
        this.card = card;
    }

    @Override
    public void beginExclusive() throws CardException {
        card.beginTransaction();
    }

    @Override
    public void disconnect(boolean reset) throws CardException {
        card.disconnect(reset ? Disposition.RESET_CARD : Disposition.LEAVE_CARD);
    }

    @Override
    public void endExclusive() throws CardException {
        card.endTransaction(Disposition.LEAVE_CARD);
    }

    @Override
    public ATR getATR() {
        try {
            return new ATR(card.getStatus().getATR());
        } catch (JSCardException e) {
            return null;
        }
    }

    @Override
    public CardChannel getBasicChannel() {
        if (basicChannel == null) {
            basicChannel = new PCSCCardChannel(0, this);
        }
        return basicChannel;
    }

    @Override
    public String getProtocol() {
        return card.getProtocol().name();
    }

    @Override
    public CardChannel openLogicalChannel() throws CardException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] transmitControlCommand(int controlCode, byte[] command) throws CardException {
        ByteBuffer sendBuffer = ByteBuffer.wrap(command);
        ByteBuffer recvBuffer = ByteBuffer.allocate(2048);
        int length = card.control(controlCode, sendBuffer, recvBuffer);

        return Arrays.copyOf(recvBuffer.array(), length);
    }

    int transmit(ByteBuffer command, ByteBuffer response) throws JSCardException {
        return card.transmit(command, response);
    }

}
