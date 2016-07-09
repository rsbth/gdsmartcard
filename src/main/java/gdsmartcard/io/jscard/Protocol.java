package gdsmartcard.io.jscard;

import com.sun.jna.platform.win32.WinDef.DWORD;

import gdsmartcard.io.winscard.WinSCardDefines;

public enum Protocol {
    T0(WinSCardDefines.SCARD_PROTOCOL_T0),
    T1(WinSCardDefines.SCARD_PROTOCOL_T1),
    ANY(WinSCardDefines.SCARD_PROTOCOL_ANY);

    DWORD value;

    Protocol(DWORD value) {
        this.value = value;
    }

    public static Protocol fromValue(DWORD value) {
        for (Protocol p : values()) {
            if (p.value.equals(value)) {
                return p;
            }
        }
        return ANY;
    }
}