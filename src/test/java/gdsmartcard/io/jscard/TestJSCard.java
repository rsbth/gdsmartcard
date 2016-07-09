package gdsmartcard.io.jscard;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TestJSCard {

    @Test
    public void test() throws JSCardException {
        JSCardContext context = JSCardContext.establish(Scope.USER);
        List<String> readerList = context.listReaders(ReaderGroup.ALL_READERS);

        for (String s : readerList) {
            System.out.println(s);
        }

        JSCard card = context.connect(readerList.get(0), ShareMode.EXCLUSIVE, Protocol.ANY);
        JSCardStatus status = card.getStatus();
        Assert.assertTrue(status.getState().ordinal() > JSCardStatus.State.POWERED.ordinal());
        byte[] atr = status.getATR();
        Assert.assertTrue(atr.length != 0);
        System.out.println(status.toString());

        ByteBuffer sendBuffer = ByteBuffer.wrap(new byte[] { 0x00, (byte) 0xa4, 0x04, 0x00 });
        ByteBuffer recvBuffer = ByteBuffer.allocate(256);
        int recvLength = card.transmit(sendBuffer, recvBuffer);
        String responseString = "<< ";
        for (int i = 0; i < recvLength; i++) {
            responseString += String.format("%02X ", recvBuffer.get(i));
        }
        System.out.println(responseString.trim());

        System.out.println(card.getProtocol());

        System.out.println(card.reconnect(ShareMode.EXCLUSIVE, Protocol.ANY, Initialization.RESET_CARD));

        card.disconnect(Disposition.RESET_CARD);
        card = context.connect(readerList.get(0), ShareMode.EXCLUSIVE, Protocol.ANY);

        for (byte b : card.getAttrib(590595)) {
            System.out.printf("%02X", b);
        }
        System.out.println();
        for (byte b : card.getAttrib(524802)) {
            System.out.printf("%02X", b);
        }
        System.out.println();
        System.out.println(Integer.reverseBytes(new BigInteger(1, card.getAttrib(524802)).intValue()) + " kHz");

        context.release();
    }

}
