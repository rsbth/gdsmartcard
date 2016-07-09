package gdsmartcard.io.jscard;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LONG;

import gdsmartcard.io.winscard.WinSCardLibrary;
import gdsmartcard.io.winscard.WinSCardReaderState;
import gdsmartcard.io.winscard.WinSCardStatus;
import gdsmartcard.io.winscard.WinSCardTypes.SCARDCONTEXT;
import gdsmartcard.io.winscard.WinSCardTypes.SCARDCONTEXTByReference;
import gdsmartcard.io.winscard.WinSCardTypes.SCARDHANDLEByReference;

public class JSCardContext {

    private WinSCardLibrary winscard;
    private SCARDCONTEXT    context;

    private JSCardContext() {
        // no direct instantiation
    }

    public static JSCardContext establish(Scope scope) throws JSCardException {
        JSCardContext jsc = new JSCardContext();
        jsc.winscard = WinSCardLibrary.load();
        SCARDCONTEXTByReference pContext = new SCARDCONTEXTByReference();
        checkSuccess(jsc.winscard.SCardEstablishContext(scope.value, null, null, pContext));
        jsc.context = pContext.getValue();
        return jsc;
    }

    public void release() throws JSCardException {
        checkSuccess(winscard.SCardReleaseContext(context));
    }

    public List<String> listReaders(ReaderGroup group) throws JSCardException {
        DWORDByReference pLength = new DWORDByReference();

        // get the length of the response string
        checkSuccess(winscard.SCardListReaders(context, group.getName(), null, pLength));

        // get the reader names
        ByteBuffer mszReaders = ByteBuffer.allocate(pLength.getValue().intValue());
        checkSuccess(winscard.SCardListReaders(context, group.getName(), mszReaders, pLength));

        return Collections.unmodifiableList(Arrays.asList(new String(mszReaders.array()).split("\\p{Cc}")));
    }

    public JSCard connect(String readerName, ShareMode mode, Protocol preferredProtocol) throws JSCardException {
        return connect(readerName, mode,
                new ArrayList<Protocol>(Arrays.asList(new Protocol[] { preferredProtocol })));
    }

    public JSCard connect(String readerName, ShareMode mode, List<Protocol> preferredProtocols) throws JSCardException {
        DWORDByReference pdwActiveProtocol = new DWORDByReference();
        SCARDHANDLEByReference phCard = new SCARDHANDLEByReference();
        long preferredProtocolsValue = 0;
        if (preferredProtocols != null) {
            for (Protocol protocol : preferredProtocols) {
                preferredProtocolsValue |= protocol.value.longValue();
            }
        }

        checkSuccess(winscard.SCardConnect(context, readerName, mode.value,
                new DWORD(preferredProtocolsValue), phCard, pdwActiveProtocol));

        return new JSCard(winscard, phCard.getValue(), pdwActiveProtocol.getValue());
    }

    static void checkSuccess(LONG status) throws JSCardException {
        if (!status.equals(WinSCardStatus.SCARD_S_SUCCESS)) {
            throw new JSCardException(status.longValue());
        }
    }

    public WinSCardReaderState[] getStatusChange(int timeout, String... readerNames) throws JSCardException {
        WinSCardReaderState[] rgReaderStates = new WinSCardReaderState[readerNames.length];
        for (int i = 0; i < readerNames.length; i++) {
            rgReaderStates[i] = new WinSCardReaderState(readerNames[i]);
        }
        checkSuccess(winscard.SCardGetStatusChange(context, new DWORD(timeout), rgReaderStates, new DWORD(readerNames.length)));

        return rgReaderStates;
    }
}
