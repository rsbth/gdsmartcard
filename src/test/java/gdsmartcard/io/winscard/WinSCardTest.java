package gdsmartcard.io.winscard;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;

import gdsmartcard.io.winscard.WinSCardTypes.SCARD_IO_REQUEST;

public class WinSCardTest extends WinSCardTestBase {

    @Test
    public void testCanReadersBeListed() throws WinSCardException {
        Assert.assertFalse(listReaders().isEmpty());
        for (String terminalName : listReaders()) {
            System.out.println(terminalName);
        }
    }

    @Test
    public void testCanAtrBeReceived() throws WinSCardException {
        DWORDByReference pcchReaderLen = new DWORDByReference();
        DWORDByReference pdwState = new DWORDByReference();
        DWORDByReference pdwProtocol = new DWORDByReference();
        ByteBuffer pbAtr = ByteBuffer.allocate(33);
        DWORDByReference pcbAtrLen = new DWORDByReference(new DWORD(pbAtr.capacity()));

        Assert.assertEquals(WinSCardStatus.SCARD_S_SUCCESS,
                winscard.SCardStatus(hCard, null, pcchReaderLen, pdwState, pdwProtocol, pbAtr, pcbAtrLen));

        for (byte b : Arrays.copyOf(pbAtr.array(), pcbAtrLen.getValue().intValue())) {
            System.out.printf("%02X ", b);
        }
        System.out.println();

        Assert.assertTrue(pcbAtrLen.getValue().intValue() > 0);
    }

    @Test
    public void testDoesCardRespondToApduCommand() throws WinSCardException {
        SCARD_IO_REQUEST pioSendPci = new SCARD_IO_REQUEST();
        pioSendPci.dwProtocol = dwActiveProtocol;
        pioSendPci.cbPciLength = new DWORD(pioSendPci.size());

        byte[] command = { 0x00, (byte) 0xa4, 0x04, 0x00 };
        ByteBuffer pbSendBuffer = ByteBuffer.wrap(command);
        ByteBuffer pbRecvBuffer = ByteBuffer.allocate(256);
        DWORDByReference pcbRecvLength = new DWORDByReference(new DWORD(pbRecvBuffer.capacity()));
        Assert.assertEquals(WinSCardStatus.SCARD_S_SUCCESS, winscard.SCardTransmit(hCard, pioSendPci, pbSendBuffer,
                new DWORD(pbSendBuffer.capacity()), null, pbRecvBuffer, pcbRecvLength));

        for (byte b : Arrays.copyOf(pbRecvBuffer.array(), pcbRecvLength.getValue().intValue())) {
            System.out.printf("%02X ", b);
        }
        System.out.println();

        Assert.assertTrue(pcbRecvLength.getValue().intValue() >= 2);
    }
}
