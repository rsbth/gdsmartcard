package gdsmartcard.io;

import java.nio.ByteBuffer;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

/**
 * {@link CardChannel} that allows transmission of raw data without the use of
 * any transmission protocol.
 */
public class RawCardChannel extends CardChannel {

    @Override
    public void close() throws CardException {
	// TODO Auto-generated method stub

    }

    @Override
    public Card getCard() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int getChannelNumber() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public ResponseAPDU transmit(CommandAPDU command) throws CardException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int transmit(ByteBuffer command, ByteBuffer response) throws CardException {
	// TODO Auto-generated method stub
	return 0;
    }

    public byte[] transmitRaw(byte[] txData) {
	// TODO implement
	return null;
    }

    public int transmitRaw(ByteBuffer txData, ByteBuffer rxData) throws CardException {
	// TODO implement
	return 0;
    }

}
