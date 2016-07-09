package gdsmartcard.globalplatform;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import gdsmartcard.AID;
import gdsmartcard.HexUtils;
import gdsmartcard.io.CommunicationListener;
import gdsmartcard.io.GDCardChannel;

public class GlobalPlatformCardChannel extends CardChannel implements CommunicationListener {

    private static final Logger               LOGGER            = Logger.getLogger(GlobalPlatformCardChannel.class.getName());

    private CardChannel                       channel;
    private Map<Integer, SecureChannelKeySet> secureChannelKeys = new HashMap<Integer, SecureChannelKeySet>();

    static {
        LOGGER.setLevel(Level.INFO);
    }

    public GlobalPlatformCardChannel(CardChannel channel) {
        this.channel = channel;
        secureChannelKeys.put(-1, SecureChannelKeySet.VISA_TEST_KEYS);
        if (channel instanceof GDCardChannel) {
            ((GDCardChannel) channel).addCommunicationListener(this);
        }
    }

    public void openSecureChannel() throws CardException {
        openSecureChannel(0);
    }

    public void openSecureChannel(int keyVersionNumber) throws CardException {
        channel = SecureCardChannel.open(this, keyVersionNumber);
    }

    public ResponseAPDU transmit(CommandAPDU command) throws CardException {
        StringBuilder s = new StringBuilder("\n>> " + HexUtils.toHexString(command.getBytes()) + "\n");

        ResponseAPDU response = channel.transmit(command);

        if (response.getNr() > 0) {
            s.append("<< " + HexUtils.toHexString(response.getData()) + "\n");
        }
        s.append(String.format("SW %02X %02X%n%n", response.getSW1(), response.getSW2()));
        LOGGER.info(s.toString());

        if (response.getSW() != 0x9000) {
            throw new ErrorResponseCardException(response);
        }
        return response;
    }

    public DeleteConfirmation delete(boolean deleteRelated, TLVObject... objects) throws CardException {
        CommandAPDU command = GlobalPlatform.deleteCommand(deleteRelated, objects);
        byte[] responseData = transmit(command).getData();
        return new DeleteConfirmation(responseData);
    }

    public DeleteConfirmation deleteCardContent(boolean deleteRelated, AID aid) throws CardException {
        CommandAPDU command = GlobalPlatform.deleteCardContentCommand(deleteRelated, aid);
        byte[] responseData = transmit(command).getData();
        return new DeleteConfirmation(responseData);
    }

    public DeleteConfirmation deleteKey(boolean deleteRelated, int identifier, int versionNumber) throws CardException {
        CommandAPDU command = GlobalPlatform.deleteKeyCommand(deleteRelated, identifier, versionNumber);
        byte[] responseData = transmit(command).getData();
        return new DeleteConfirmation(responseData);
    }

    public FileControlInformation selectIssuerSecurityDomain() throws CardException {
        try {
            byte[] responseData = selectFirstOrOnly(null);
            return new FileControlInformation(responseData);
        } catch (MalformedFCIException e) {
            throw new CardException(e);
        }
    }

    public byte[] selectFirstOrOnly(AID aid) throws CardException {
        return transmit(GlobalPlatform.selectFirstOrOnlyCommand(aid)).getData();
    }

    public byte[] selectNext(AID aid) throws CardException {
        return transmit(GlobalPlatform.selectNextCommand(aid)).getData();
    }

    public void setStatusOfISD(ISDLifeCycleState state) throws CardException {
        transmit(GlobalPlatform.setStatusOfISDCommand(state));
    }

    public void setStatusOfApplication(ApplicationLifeCycleState state, AID aid) throws CardException {
        transmit(GlobalPlatform.setStatusOfApplicationCommand(state, aid));
    }

    public byte[] getData(int tag) throws CardException {
        return transmit(GlobalPlatform.getDataCommand(tag)).getData();
    }

    public byte[] getData(ISDTag tag) throws CardException {
        return getData(tag.getValue());
    }

    public ISDLifeCycleState getStatusOfISD() throws CardException {
        transmit(GlobalPlatform.getStatusCommand(
                GlobalPlatform.P1_GET_STATUS_ISD_ONLY, GlobalPlatform.P2_GET_STATUS_FIRST_OR_ALL));
        return null;
    }

    public byte[] getStatusOfExecutableLoadFiles(AID aid) throws CardException {
        try {
            return transmit(GlobalPlatform.getStatusCommand(
                    GlobalPlatform.P1_GET_STATUS_EXECUTABLE_LOAD_FILES,
                    GlobalPlatform.P2_GET_STATUS_FIRST_OR_ALL, aid)).getData();
        } catch (ErrorResponseCardException e) {
            return null;
        }
    }

    public byte[] getStatusOfApplications(AID aid) throws CardException {
        try {
            return transmit(GlobalPlatform.getStatusCommand(
                    GlobalPlatform.P1_GET_STATUS_APPLICATIONS,
                    GlobalPlatform.P2_GET_STATUS_FIRST_OR_ALL, aid)).getData();
        } catch (ErrorResponseCardException e) {
            return null;
        }
    }

    public SecureChannelKeySet getKeySet(int keyVersionNumber) {
        return secureChannelKeys.get(keyVersionNumber);
    }

    public boolean isAIDPresentOnCard(AID aid) throws CardException {
        selectIssuerSecurityDomain();
        boolean isExecutableLoadFilePresent = (getStatusOfExecutableLoadFiles(aid) != null);
        boolean isApplicationPresent = (getStatusOfApplications(aid) != null);
        return (isExecutableLoadFilePresent || isApplicationPresent);
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
    public int transmit(ByteBuffer arg0, ByteBuffer arg1) throws CardException {
        return channel.transmit(arg0, arg1);
    }

    @Override
    public void transmitting(CommandAPDU command) {
        System.out.print(">>");
        for (byte b : command.getBytes()) {
            System.out.printf(" %02X", b);
        }
        System.out.println();
    }

    @Override
    public void received(ResponseAPDU response) {
        if (response.getNr() > 0) {
            System.out.print("<<");
            for (byte b : response.getData()) {
                System.out.printf(" %02X", b);
            }
            System.out.println();
        }
        System.out.printf("SW %02X %02X%n%n", response.getSW1(), response.getSW2());
    }
}
