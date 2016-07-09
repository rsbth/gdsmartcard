package gdsmartcard.io.jscard;

import com.sun.jna.platform.win32.WinDef.DWORD;

import gdsmartcard.io.winscard.WinSCardDefines;

public enum Initialization {
    LEAVE_CARD(WinSCardDefines.SCARD_LEAVE_CARD),
    RESET_CARD(WinSCardDefines.SCARD_RESET_CARD),
    UNPOWER_CARD(WinSCardDefines.SCARD_UNPOWER_CARD);

    DWORD value;

    Initialization(DWORD value) {
        this.value = value;
    }
}