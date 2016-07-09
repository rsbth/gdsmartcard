/*
 * To the extent possible under law, contributors have waived all
 * copyright and related or neighboring rights to work.
 */
package gdsmartcard.io.winscard;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.platform.win32.WinDef.DWORD;

public class WinSCardDefines {

    private WinSCardDefines() {
    }

    private static ByteBuffer allocateMultiStringByteBuffer(String value) {
        return (ByteBuffer) ByteBuffer.allocate(value.length() + 2).put(value.getBytes()).rewind();
    }

    /**
     * Group used when no group name is provided when listing readers. Returns a list of all readers, regardless of what
     * group or groups the readers are in.
     */
    public static final ByteBuffer SCARD_ALL_READERS = allocateMultiStringByteBuffer("SCard$AllReaders");

    /**
     * Default group to which all readers are added when introduced into the system.
     */
    public static final ByteBuffer SCARD_DEFAULT_READERS = allocateMultiStringByteBuffer("SCard$DefaultReaders");

    /**
     * Unused legacy value. This is an internally managed group that cannot be modified by using any reader group APIs.
     * It is intended to be used for enumeration only.
     */
    public static final ByteBuffer SCARD_LOCAL_READERS = allocateMultiStringByteBuffer("SCard$LocalReaders");

    /**
     * Unused legacy value. This is an internally managed group that cannot be modified by using any reader group APIs.
     * It is intended to be used for enumeration only.
     */
    public static final ByteBuffer SCARD_SYSTEM_READERS = allocateMultiStringByteBuffer("SCard$SystemReaders");

    /** Database operations are performed within the domain of the user. */
    public static DWORD SCARD_SCOPE_USER = new DWORD(0);

    /**
     * Database operations are performed within the domain of the system. The calling application must have appropriate
     * access permissions for any database actions.
     */
    public static DWORD SCARD_SCOPE_SYSTEM = new DWORD(2);

    // Bit masks used by waitFor* methods for SCardGetStatusChange
    public static final int SCARD_STATE_UNAWARE     = 0x0000;
    public static final int SCARD_STATE_IGNORE      = 0x0001;
    public static final int SCARD_STATE_CHANGED     = 0x0002;
    public static final int SCARD_STATE_UNKNOWN     = 0x0004;
    public static final int SCARD_STATE_UNAVAILABLE = 0x0008;
    public static final int SCARD_STATE_EMPTY       = 0x0010;
    public static final int SCARD_STATE_PRESENT     = 0x0020;
    public static final int SCARD_STATE_ATRMATCH    = 0x0040;
    public static final int SCARD_STATE_EXCLUSIVE   = 0x0080;
    public static final int SCARD_STATE_INUSE       = 0x0100;
    public static final int SCARD_STATE_MUTE        = 0x0200;
    public static final int SCARD_STATE_UNPOWERED   = 0x0400;

    /** Exclusive mode only. */
    public static final DWORD SCARD_SHARE_EXCLUSIVE = new DWORD(0x00000001);

    /** Shared mode only. */
    public static final DWORD SCARD_SHARE_SHARED = new DWORD(0x00000002);

    /** Raw mode only. */
    public static final DWORD SCARD_SHARE_DIRECT = new DWORD(0x00000003);

    /** protocol not set */
    public static final DWORD SCARD_PROTOCOL_UNDEFINED = new DWORD(0x00000000);

    /** T=0 active protocol. */
    public static final DWORD SCARD_PROTOCOL_T0 = new DWORD(0x00000001);

    /** T=1 active protocol. */
    public static final DWORD SCARD_PROTOCOL_T1 = new DWORD(0x00000002);

    /** Raw active protocol. */
    public static final DWORD SCARD_PROTOCOL_RAW = new DWORD(0x00000004);

    /** T=15 protocol. */
    public static final DWORD SCARD_PROTOCOL_T15 = new DWORD(0x00000008);

    /** IFD determines prot. */
    public static final DWORD SCARD_PROTOCOL_ANY = new DWORD(
            SCARD_PROTOCOL_T0.longValue() | SCARD_PROTOCOL_T1.longValue());

    /** Do nothing on close. */
    public static final DWORD SCARD_LEAVE_CARD = new DWORD(0x0000);

    /** Reset on close. */
    public static final DWORD SCARD_RESET_CARD = new DWORD(0x0001);

    /** Power down on close. */
    public static final DWORD SCARD_UNPOWER_CARD = new DWORD(0x0002);

    /** Eject on close. */
    public static final DWORD SCARD_EJECT_CARD = new DWORD(0x0003);

    /** There is no card in the reader. */
    public static final int SCARD_ABSENT = 1;

    /**
     * There is a card in the reader, but it has not been moved into position for use.
     */
    public static final int SCARD_PRESENT      = 2;
    public static final int SCARD_SWALLOWED    = 3;
    public static final int SCARD_POWERED      = 4;
    public static final int SCARD_NEGOTIABLE   = 5;
    public static final int SCARD_SPECIFICMODE = 6;

    public static List<String> stateToStrings(int scardState) {
        if (0 == scardState)
            return Arrays.asList("unaware");
        List<String> r = new ArrayList<String>();
        if (0 != (scardState & SCARD_STATE_IGNORE))
            r.add("ignore");
        if (0 != (scardState & SCARD_STATE_CHANGED))
            r.add("changed");
        if (0 != (scardState & SCARD_STATE_UNKNOWN))
            r.add("unknown");
        if (0 != (scardState & SCARD_STATE_UNAVAILABLE))
            r.add("unavailable");
        if (0 != (scardState & SCARD_STATE_EMPTY))
            r.add("empty");
        if (0 != (scardState & SCARD_STATE_PRESENT))
            r.add("present");
        if (0 != (scardState & SCARD_STATE_ATRMATCH))
            r.add("atrmatch");
        if (0 != (scardState & SCARD_STATE_EXCLUSIVE))
            r.add("exclusive");
        if (0 != (scardState & SCARD_STATE_INUSE))
            r.add("inuse");
        if (0 != (scardState & SCARD_STATE_MUTE))
            r.add("mute");
        if (0 != (scardState & SCARD_STATE_UNPOWERED))
            r.add("unpowered");
        return r;
    }

    /** Infinite timeout for SCardGetStatusChange */
    public static final int    INFINITE      = 0xffffffff;
    public static final int    MAX_ATR_SIZE  = 33;
    public static final String PNP_READER_ID = "\\\\?PnP?\\Notification";

    public static final int SCARD_ATTR_VENDOR_NAME              = 65792;
    public static final int SCARD_ATTR_VENDOR_IFD_TYPE          = 65793;
    public static final int SCARD_ATTR_VENDOR_IFD_VERSION       = 65794;
    public static final int SCARD_ATTR_VENDOR_IFD_SERIAL_NO     = 65795;
    public static final int SCARD_ATTR_CHANNEL_ID               = 131344;
    public static final int SCARD_ATTR_ASYNC_PROTOCOL_TYPES     = 196896;
    public static final int SCARD_ATTR_DEFAULT_CLK              = 196897;
    public static final int SCARD_ATTR_MAX_CLK                  = 196898;
    public static final int SCARD_ATTR_DEFAULT_DATA_RATE        = 196899;
    public static final int SCARD_ATTR_MAX_DATA_RATE            = 196900;
    public static final int SCARD_ATTR_MAX_IFSD                 = 196901;
    public static final int SCARD_ATTR_SYNC_PROTOCOL_TYPES      = 196902;
    public static final int SCARD_ATTR_POWER_MGMT_SUPPORT       = 262449;
    public static final int SCARD_ATTR_USER_TO_CARD_AUTH_DEVICE = 328000;
    public static final int SCARD_ATTR_USER_AUTH_INPUT_DEVICE   = 328002;
    public static final int SCARD_ATTR_CHARACTERISTICS          = 393552;
    public static final int SCARD_ATTR_CURRENT_PROTOCOL_TYPE    = 524801;
    public static final int SCARD_ATTR_CURRENT_CLK              = 524802;
    public static final int SCARD_ATTR_CURRENT_F                = 524803;
    public static final int SCARD_ATTR_CURRENT_D                = 524804;
    public static final int SCARD_ATTR_CURRENT_N                = 524805;
    public static final int SCARD_ATTR_CURRENT_W                = 524806;
    public static final int SCARD_ATTR_CURRENT_IFSC             = 524807;
    public static final int SCARD_ATTR_CURRENT_IFSD             = 524808;
    public static final int SCARD_ATTR_CURRENT_BWT              = 524809;
    public static final int SCARD_ATTR_CURRENT_CWT              = 524810;
    public static final int SCARD_ATTR_CURRENT_EBC_ENCODING     = 524811;
    public static final int SCARD_ATTR_EXTENDED_BWT             = 524812;
    public static final int SCARD_ATTR_ICC_PRESENCE             = 590592;
    public static final int SCARD_ATTR_ICC_INTERFACE_STATUS     = 590593;
    public static final int SCARD_ATTR_CURRENT_IO_STATE         = 590594;
    public static final int SCARD_ATTR_ATR_STRING               = 590595;
    public static final int SCARD_ATTR_ICC_TYPE_PER_ATR         = 590596;
    public static final int SCARD_ATTR_ESC_RESET                = 499712;
    public static final int SCARD_ATTR_ESC_CANCEL               = 499715;
    public static final int SCARD_ATTR_ESC_AUTHREQUEST          = 499717;
    public static final int SCARD_ATTR_MAXINPUT                 = 499719;
    public static final int SCARD_ATTR_DEVICE_UNIT              = 2147418113;
    public static final int SCARD_ATTR_DEVICE_IN_USE            = 2147418114;
    public static final int SCARD_ATTR_DEVICE_FRIENDLY_NAME_A   = 2147418115;
    public static final int SCARD_ATTR_DEVICE_SYSTEM_NAME_A     = 2147418116;
    public static final int SCARD_ATTR_DEVICE_FRIENDLY_NAME_W   = 2147418117;
    public static final int SCARD_ATTR_DEVICE_SYSTEM_NAME_W     = 2147418118;
    public static final int SCARD_ATTR_SUPRESS_T1_IFS_REQUEST   = 2147418119;
    public static final int SCARD_ATTR_DEVICE_FRIENDLY_NAME     = 2147418115;
    public static final int SCARD_ATTR_DEVICE_SYSTEM_NAME       = 2147418116;

}
