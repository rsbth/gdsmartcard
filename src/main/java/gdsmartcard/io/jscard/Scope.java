package gdsmartcard.io.jscard;

import com.sun.jna.platform.win32.WinDef.DWORD;

import gdsmartcard.io.winscard.WinSCardDefines;

public enum Scope {
    USER(WinSCardDefines.SCARD_SCOPE_USER), SYSTEM(WinSCardDefines.SCARD_SCOPE_USER);

    DWORD value;

    Scope(DWORD value) {
        this.value = value;
    }
}