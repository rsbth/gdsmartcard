package gdsmartcard.io.winscard;

import java.nio.ByteBuffer;
import java.util.HashMap;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.LPVOID;

import gdsmartcard.io.winscard.WinSCardTypes.SCARDCONTEXT;
import gdsmartcard.io.winscard.WinSCardTypes.SCARDCONTEXTByReference;
import gdsmartcard.io.winscard.WinSCardTypes.SCARDHANDLE;
import gdsmartcard.io.winscard.WinSCardTypes.SCARDHANDLEByReference;
import gdsmartcard.io.winscard.WinSCardTypes.SCARD_IO_REQUEST;

public interface WinSCardLibrary extends Library {

    LONG SCardEstablishContext(DWORD dwScope, final LPVOID pvReserved1, LPVOID pvReserved2, SCARDCONTEXTByReference phContext);

    LONG SCardReleaseContext(SCARDCONTEXT hContext);

    LONG SCardConnect(SCARDCONTEXT hContext, String szReader, DWORD dwSharMode, DWORD dwPreferredProtocols, SCARDHANDLEByReference phCard, DWORDByReference pdwActiveProtocol);

    LONG SCardReconnect(SCARDHANDLE hCard, DWORD dwShareMode, DWORD dwPreferredProtocols, DWORD dwInitialization, DWORDByReference pdwActiveProtocol);

    LONG SCardDisconnect(SCARDHANDLE hCard, DWORD dwDisposition);

    LONG SCardBeginTransaction(SCARDHANDLE hCard);

    LONG SCardEndTransaction(SCARDHANDLE hCard, DWORD dwDisposition);

    LONG SCardStatus(SCARDHANDLE hCard, ByteBuffer mszReaderName, DWORDByReference pcchReaderLen, DWORDByReference pdwState, DWORDByReference pdwProtocol, ByteBuffer pbAtr, DWORDByReference pcbAtrLen);

    LONG SCardGetStatusChange(SCARDCONTEXT hContext, DWORD dwTimeout, WinSCardReaderState[] rgReaderStates, DWORD cReaders);

    LONG SCardControl(SCARDHANDLE hCard, DWORD dwControlCode, ByteBuffer pbSendBuffer, DWORD cbSendLength, ByteBuffer pbRecvBuffer, DWORD cbRecvLength, DWORDByReference lpBytesReturned);

    LONG SCardGetAttrib(SCARDHANDLE hCard, DWORD dwAttrId, ByteBuffer pbAttr, DWORDByReference pcbAttrLen);

    LONG SCardSetAttrib(SCARDHANDLE hCard, DWORD dwAttrId, ByteBuffer pbAttr, DWORD cbAttrLen);

    LONG SCardTransmit(SCARDHANDLE hCard, SCARD_IO_REQUEST pioSendPci, ByteBuffer pbSendBuffer, DWORD cbSendLength, SCARD_IO_REQUEST pioRecvPci, ByteBuffer pbRecvBuffer, DWORDByReference pcbRecvLength);

    LONG SCardListReaders(SCARDCONTEXT hContext, ByteBuffer mszGroups, ByteBuffer mszReaders, DWORDByReference pcchReaders);

    LONG SCardFreeMemory(SCARDCONTEXT hContext, Pointer pvMem);

    LONG SCardListReaderGroups(SCARDCONTEXT hContext, ByteBuffer mszGroups, DWORDByReference pcchGroups);

    LONG SCardCancel(SCARDCONTEXT hContext);

    LONG SCardIsValidContext(SCARDCONTEXT hContext);

    public static WinSCardLibrary load() {
        HashMap<Object, Object> options = new HashMap<Object, Object>();
        options.put(Library.OPTION_FUNCTION_MAPPER, new WinSCardFunctionMapper());
        return (WinSCardLibrary) Native.loadLibrary("WinSCard.dll", WinSCardLibrary.class, options);
    }
}
