package gdsmartcard.io;

import java.nio.ByteBuffer;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

public class WrappingCardChannel extends CardChannel {

    protected CardChannel channel;

    protected WrappingCardChannel(CardChannel channel) {
	this.channel = channel;
    }

    @Override
    public void close() throws CardException {
	channel.close();
    }

    @Override
    public Card getCard() {
	return channel.getCard();
    }

    @Override
    public int getChannelNumber() {
	return channel.getChannelNumber();
    }

    @Override
    public ResponseAPDU transmit(CommandAPDU command) throws CardException {
	return channel.transmit(command);
    }

    @Override
    public int transmit(ByteBuffer command, ByteBuffer response) throws CardException {
	return channel.transmit(command, response);
    }

}
