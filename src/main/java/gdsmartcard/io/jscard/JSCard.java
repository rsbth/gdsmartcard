package gdsmartcard.io.jscard;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;

import gdsmartcard.io.winscard.WinSCardLibrary;
import gdsmartcard.io.winscard.WinSCardTypes.SCARDHANDLE;
import gdsmartcard.io.winscard.WinSCardTypes.SCARD_IO_REQUEST;

public class JSCard {

    private static final int ATR_MAX_LENGTH = 33;
    private WinSCardLibrary  winscard;
    private SCARDHANDLE      handle;
    private DWORD            protocol;

    JSCard(WinSCardLibrary winscard, SCARDHANDLE handle, DWORD protocol) {
        this.winscard = winscard;
        this.handle = handle;
        this.protocol = protocol;
    }

    public Protocol getProtocol() {
        return Protocol.fromValue(protocol);
    }

    public JSCardStatus getStatus() throws JSCardException {
        DWORDByReference pcchReaderLen = new DWORDByReference();
        DWORDByReference pdwState = new DWORDByReference();
        DWORDByReference pdwProtocol = new DWORDByReference();
        ByteBuffer pbAtr = ByteBuffer.allocate(ATR_MAX_LENGTH);
        DWORDByReference pcbAtrLen = new DWORDByReference(new DWORD(pbAtr.capacity()));

        JSCardContext.checkSuccess(
                winscard.SCardStatus(handle, null, pcchReaderLen, pdwState, pdwProtocol, pbAtr, pcbAtrLen));

        ByteBuffer szReaderName = ByteBuffer.allocate(pcchReaderLen.getValue().intValue());
        JSCardContext.checkSuccess(
                winscard.SCardStatus(handle, szReaderName, pcchReaderLen, pdwState, pdwProtocol, pbAtr, pcbAtrLen));

        return new JSCardStatus(new String(szReaderName.array()).trim(), pdwState.getValue(),
                Arrays.copyOf(pbAtr.array(), pcbAtrLen.getValue().intValue()));
    }

    public Protocol reconnect(ShareMode shareMode, Protocol preferredProtocol, Initialization initialization) throws JSCardException {
        DWORDByReference pdwActiveProtocol = new DWORDByReference();
        JSCardContext.checkSuccess(winscard.SCardReconnect(handle, shareMode.value,
                preferredProtocol.value, initialization.value, pdwActiveProtocol));
        protocol = pdwActiveProtocol.getValue();
        return getProtocol();
    }

    public void disconnect(Disposition disposition) throws JSCardException {
        JSCardContext.checkSuccess(winscard.SCardDisconnect(handle, disposition.value));
    }

    public void beginTransaction() throws JSCardException {
        JSCardContext.checkSuccess(winscard.SCardBeginTransaction(handle));
    }

    public void endTransaction(Disposition disposition) throws JSCardException {
        JSCardContext.checkSuccess(winscard.SCardEndTransaction(handle, disposition.value));
    }

    public int control(int code, ByteBuffer sendBuffer, ByteBuffer recvBuffer) throws JSCardException {
        DWORDByReference lpBytesReturned = new DWORDByReference();
        JSCardContext.checkSuccess(winscard.SCardControl(handle,
                new DWORD(code), sendBuffer, new DWORD(sendBuffer.remaining()),
                recvBuffer, new DWORD(recvBuffer.capacity()), lpBytesReturned));
        return lpBytesReturned.getValue().intValue();
    }

    public byte[] getAttrib(int attrId) throws JSCardException {
        DWORD dwAttrId = new DWORD(attrId);
        DWORDByReference pcbAttrLen = new DWORDByReference();
        JSCardContext.checkSuccess(winscard.SCardGetAttrib(handle, dwAttrId, null, pcbAttrLen));
        ByteBuffer pbAttr = ByteBuffer.allocate(pcbAttrLen.getValue().intValue());
        JSCardContext.checkSuccess(winscard.SCardGetAttrib(handle, dwAttrId, pbAttr, pcbAttrLen));
        return pbAttr.array();
    }

    public void setAttrib(int attrId, byte[] attr) throws JSCardException {
        JSCardContext.checkSuccess(winscard.SCardSetAttrib(handle,
                new DWORD(attrId), ByteBuffer.wrap(attr), new DWORD(attr.length)));
    }

    public int transmit(ByteBuffer sendBuffer, ByteBuffer recvBuffer) throws JSCardException {
        SCARD_IO_REQUEST pioSendPci = new SCARD_IO_REQUEST();
        pioSendPci.dwProtocol = protocol;
        pioSendPci.cbPciLength = new DWORD(pioSendPci.size());

        DWORDByReference pcbRecvLength = new DWORDByReference(new DWORD(recvBuffer.capacity()));
        JSCardContext.checkSuccess(winscard.SCardTransmit(handle, pioSendPci, sendBuffer,
                new DWORD(sendBuffer.capacity()), null, recvBuffer, pcbRecvLength));

        return pcbRecvLength.getValue().intValue();
    }

}
