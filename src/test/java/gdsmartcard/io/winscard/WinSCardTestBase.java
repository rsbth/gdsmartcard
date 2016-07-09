package gdsmartcard.io.winscard;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LONG;

import gdsmartcard.io.winscard.WinSCardTypes.SCARDCONTEXT;
import gdsmartcard.io.winscard.WinSCardTypes.SCARDCONTEXTByReference;
import gdsmartcard.io.winscard.WinSCardTypes.SCARDHANDLE;
import gdsmartcard.io.winscard.WinSCardTypes.SCARDHANDLEByReference;

public abstract class WinSCardTestBase {

    protected static WinSCardLibrary winscard;
    protected static SCARDCONTEXT    context;

    @BeforeClass
    public static void setUpClass() throws WinSCardException {
        winscard = WinSCardLibrary.load();
        SCARDCONTEXTByReference pContext = new SCARDCONTEXTByReference();
        checkSuccess(winscard.SCardEstablishContext(WinSCardDefines.SCARD_SCOPE_SYSTEM, null, null, pContext));
        context = pContext.getValue();
    }

    @AfterClass
    public static void tearDownClass() throws WinSCardException {
        if (context != null) {
            checkSuccess(winscard.SCardReleaseContext(context));
        }
    }

    @Before
    public void setUp() throws WinSCardException {
        hCard = connectToFirstReader();
    }

    @After
    public void tearDown() throws WinSCardException {
        disconnect(hCard);
    }

    protected static DWORD dwActiveProtocol;
    protected SCARDHANDLE  hCard;

    private static SCARDHANDLE connectToFirstReader() throws WinSCardException {
        return connect(listReaders().get(0));
    }

    private static SCARDHANDLE connect(String readerName) throws WinSCardException {
        DWORDByReference pdwActiveProtocol = new DWORDByReference();

        SCARDHANDLEByReference phCard = new SCARDHANDLEByReference();
        checkSuccess(winscard.SCardConnect(context, readerName, WinSCardDefines.SCARD_SHARE_EXCLUSIVE,
                WinSCardDefines.SCARD_PROTOCOL_ANY, phCard, pdwActiveProtocol));
        SCARDHANDLE hCard = phCard.getValue();
        dwActiveProtocol = pdwActiveProtocol.getValue();

        return hCard;
    }

    private static void disconnect(SCARDHANDLE hCard) throws WinSCardException {
        checkSuccess(winscard.SCardDisconnect(hCard, WinSCardDefines.SCARD_UNPOWER_CARD));
    }

    protected static List<String> listReaders() throws WinSCardException {
        DWORDByReference pLength = new DWORDByReference();
        // get the length of the response string
        assertSuccess(winscard.SCardListReaders(context, WinSCardDefines.SCARD_ALL_READERS, null, pLength));

        // get the reader names
        ByteBuffer mszReaders = ByteBuffer.allocate(pLength.getValue().intValue());
        checkSuccess(winscard.SCardListReaders(context, WinSCardDefines.SCARD_ALL_READERS, mszReaders, pLength));
        return Collections.unmodifiableList(Arrays.asList(new String(mszReaders.array()).split("\\p{Cc}")));
    }

    private static void checkSuccess(LONG status) throws WinSCardException {
        if (!status.equals(WinSCardStatus.SCARD_S_SUCCESS)) {
            throw new WinSCardException(status.longValue());
        }
    }

    private static void assertSuccess(LONG status) {
        Assert.assertEquals(String.format("WinSCard function returned error 0x%08x", status.intValue()),
                WinSCardStatus.SCARD_S_SUCCESS, status);
    }
}