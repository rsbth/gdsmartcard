package gdsmartcard.io;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;

public class GDCard extends Card {

    @Override
    public void beginExclusive() throws CardException {
	// TODO Auto-generated method stub

    }

    @Override
    public void disconnect(boolean reset) throws CardException {
	// TODO Auto-generated method stub

    }

    @Override
    public void endExclusive() throws CardException {
	// TODO Auto-generated method stub

    }

    @Override
    public ATR getATR() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public CardChannel getBasicChannel() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getProtocol() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public CardChannel openLogicalChannel() throws CardException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public byte[] transmitControlCommand(int controlCode, byte[] command) throws CardException {
	// TODO Auto-generated method stub
	return null;
    }

    /**
     * Returns a {@link RawCardChannel} for raw data transmission.
     * 
     * @return the {@link RawCardChannel} for raw data transmission
     */
    public RawCardChannel getRawChannel() {
	// TODO implement
	return null;
    }

}
