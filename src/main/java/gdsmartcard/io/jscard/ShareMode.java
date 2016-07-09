package gdsmartcard.io.jscard;

import com.sun.jna.platform.win32.WinDef.DWORD;

import gdsmartcard.io.winscard.WinSCardDefines;

public enum ShareMode {
    EXCLUSIVE(WinSCardDefines.SCARD_SHARE_EXCLUSIVE),
    SHARED(WinSCardDefines.SCARD_SHARE_SHARED),
    DIRECT(WinSCardDefines.SCARD_SHARE_DIRECT);

    DWORD value;

    ShareMode(DWORD value) {
        this.value = value;
    }
}