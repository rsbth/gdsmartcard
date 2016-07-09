package gdsmartcard.io.winscard;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.DWORD;

public class WinSCardReaderState extends Structure {

    private static final int ALIGN          = Platform.isMac() ? ALIGN_NONE : ALIGN_DEFAULT;
    public String            szReader;
    public Pointer           pvUserData;
    public DWORD             dwCurrentState = new DWORD(0);
    public DWORD             dwEventState   = new DWORD(0);
    public DWORD             cbAtr          = new DWORD(0);
    public byte[]            rgbAtr         = new byte[WinSCardDefines.MAX_ATR_SIZE];

    public WinSCardReaderState() {
        super(null, ALIGN);
    }

    public WinSCardReaderState(String readerName) {
        this.szReader = readerName;
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("szReader", "pvUserData", "dwCurrentState", "dwEventState", "cbAtr", "rgbAtr");
    }

    @Override
    public String toString() {
        return String.format("%s: %s", szReader, getStateStrings());
    }

    private List<String> getStateStrings() {
        Vector<String> states = new Vector<String>();
        int state = dwEventState.intValue();
        if (state == WinSCardDefines.SCARD_STATE_UNAWARE) {
            states.add("unaware");
        }
        if ((state & WinSCardDefines.SCARD_STATE_IGNORE) != 0) {
            states.add("ignore");
        }
        if ((state & WinSCardDefines.SCARD_STATE_CHANGED) != 0) {
            states.add("changed");
        }
        if ((state & WinSCardDefines.SCARD_STATE_UNKNOWN) != 0) {
            states.add("unknown");
        }
        if ((state & WinSCardDefines.SCARD_STATE_UNAVAILABLE) != 0) {
            states.add("unavailable");
        }
        if ((state & WinSCardDefines.SCARD_STATE_EMPTY) != 0) {
            states.add("empty");
        }
        if ((state & WinSCardDefines.SCARD_STATE_PRESENT) != 0) {
            states.add("present");
        }
        if ((state & WinSCardDefines.SCARD_STATE_ATRMATCH) != 0) {
            states.add("ATR match");
        }
        if ((state & WinSCardDefines.SCARD_STATE_EXCLUSIVE) != 0) {
            states.add("exclusive");
        }
        if ((state & WinSCardDefines.SCARD_STATE_INUSE) != 0) {
            states.add("in use");
        }
        if ((state & WinSCardDefines.SCARD_STATE_MUTE) != 0) {
            states.add("mute");
        }
        if ((state & WinSCardDefines.SCARD_STATE_UNPOWERED) != 0) {
            states.add("unpowered");
        }

        return states;
    }
}
