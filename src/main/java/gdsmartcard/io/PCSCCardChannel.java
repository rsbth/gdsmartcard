package gdsmartcard.io;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

public class PCSCCardChannel extends CardChannel implements GDCardChannel {

    private int                         channelNumber;
    private PCSCCard                    card;
    private ByteBuffer                  receiveBuffer = ByteBuffer.allocate(2048);
    private List<CommunicationListener> listeners     = new Vector<CommunicationListener>();

    public PCSCCardChannel(int channelNumber, PCSCCard card) {
        this.channelNumber = channelNumber;
        this.card = card;
    }

    @Override
    public void close() throws CardException {
        // TODO Auto-generated method stub

    }

    @Override
    public Card getCard() {
        return card;
    }

    @Override
    public int getChannelNumber() {
        return channelNumber;
    }

    @Override
    public ResponseAPDU transmit(CommandAPDU command) throws CardException {
        fireTransmitting(command);
        receiveBuffer.rewind();
        int length = card.transmit(ByteBuffer.wrap(command.getBytes()), receiveBuffer);
        ResponseAPDU response = new ResponseAPDU(Arrays.copyOf(receiveBuffer.array(), length));
        fireReceived(response);

        if (response.getSW1() == 0x6C) {
            response = transmit(new CommandAPDU(command.getCLA(), command.getINS(), command.getP1(), command.getP2(),
                    command.getData(), response.getSW2()));
        }

        return response;
    }

    @Override
    public int transmit(ByteBuffer command, ByteBuffer response) throws CardException {
        ResponseAPDU responseAPDU = transmit(new CommandAPDU(command));
        response.put(responseAPDU.getBytes());
        return responseAPDU.getBytes().length;
    }

    /* (non-Javadoc)
     * @see gdsmartcard.io.GDCardChannel#addCommunicationListener(gdsmartcard.io.CommunicationListener)
     */
    @Override
    public boolean addCommunicationListener(CommunicationListener l) {
        if (listeners.contains(l)) {
            return false;
        }
        return listeners.add(l);
    }

    /* (non-Javadoc)
     * @see gdsmartcard.io.GDCardChannel#removeCommunicationListener(gdsmartcard.io.CommunicationListener)
     */
    @Override
    public boolean removeCommunicationListener(CommunicationListener l) {
        return listeners.remove(l);
    }

    private void fireTransmitting(CommandAPDU command) {
        for (CommunicationListener l : listeners) {
            l.transmitting(command);
        }
    }

    private void fireReceived(ResponseAPDU response) {
        for (CommunicationListener l : listeners) {
            l.received(response);
        }
    }

}
