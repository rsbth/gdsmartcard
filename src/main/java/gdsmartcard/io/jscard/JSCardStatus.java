package gdsmartcard.io.jscard;

import com.sun.jna.platform.win32.WinDef.DWORD;

import gdsmartcard.io.winscard.WinSCardDefines;

public class JSCardStatus {

    public enum State {
        UNKNOWN, ABSENT, PRESENT, SWALLOWED, POWERED, NEGOTIABLE, SPECIFICMODE;
    }

    private String readerName;
    private DWORD  state;
    private byte[] atr;

    JSCardStatus(String readerName, DWORD state, byte[] atr) {
        this.readerName = readerName;
        this.state = state;
        this.atr = atr;
    }

    public String getReaderName() {
        return readerName;
    }

    public byte[] getATR() {
        return atr;
    }

    public State getState() {
        switch (state.intValue()) {
        case WinSCardDefines.SCARD_ABSENT:
            return State.ABSENT;
        case WinSCardDefines.SCARD_PRESENT:
            return State.PRESENT;
        case WinSCardDefines.SCARD_SWALLOWED:
            return State.SWALLOWED;
        case WinSCardDefines.SCARD_POWERED:
            return State.POWERED;
        case WinSCardDefines.SCARD_NEGOTIABLE:
            return State.NEGOTIABLE;
        case WinSCardDefines.SCARD_SPECIFICMODE:
            return State.SPECIFICMODE;
        default:
            return State.UNKNOWN;
        }
    }

    @Override
    public String toString() {
        String atrString = new String();
        for (byte b : getATR()) {
            atrString += String.format("%02X ", b);
        }
        return String.format("reader: %s, state: %s, ATR: %s", getReaderName(), getState(), atrString.trim());
    }
}
