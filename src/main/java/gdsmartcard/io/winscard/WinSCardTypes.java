package gdsmartcard.io.winscard;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.IntegerType;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.ptr.ByReference;

public class WinSCardTypes {

    /**
     * Base class for handles used in PC/SC. On Windows, it is a handle
     * (ULONG_PTR which cannot be dereferenced). On PCSC, it is an integer
     * (int32_t on OS X, long on Linux).
     */
    private static abstract class Handle extends IntegerType {

        private static final long serialVersionUID = 1L;
        public static final int SIZE = Pointer.SIZE;

        public Handle(long value) {
            super(SIZE, value);
        }

        @Override
        public String toString() {
            return String.format("%s{%x}", getClass().getSimpleName(), longValue());
        }
    }

    /**
     * The SCARDCONTEXT type defined in WinSCard.h, used for most SCard
     * functions.
     */
    public static class SCARDCONTEXT extends Handle {

        private static final long serialVersionUID = 1L;

        /**
         * no-arg constructor needed for
         * {@link NativeMappedConverter#defaultValue()}
         */
        public SCARDCONTEXT() {
            this(0l);
        }

        public SCARDCONTEXT(long value) {
            super(value);
        }
    }

    /** Pointer to a handle. */
    private static abstract class HandleByReference extends ByReference {

        public HandleByReference() {
            super(Handle.SIZE);
        }

        protected long getLong() {
            long v = Handle.SIZE == 4 ? getPointer().getInt(0) : getPointer().getLong(0);
            return v;
        }

        protected void setLong(long value) {
            if (Handle.SIZE == 4) {
                getPointer().setInt(0, (int) value);
            } else {
                getPointer().setLong(0, value);
            }
        }
    }

    /** PSCARDCONTEXT used for SCardEstablishContext. */
    public static class SCARDCONTEXTByReference extends HandleByReference {

        public SCARDCONTEXTByReference() {
            super();
        }

        public SCARDCONTEXT getValue() {
            return new SCARDCONTEXT(getLong());
        }

        public void setValue(SCARDCONTEXT context) {
            setLong(context.longValue());
        }
    }

    /**
     * The SCARDHANDLE type defined in WinSCard.h. It represents a connection to
     * a card.
     */
    public static class SCARDHANDLE extends Handle {

        private static final long serialVersionUID = 1L;

        /**
         * no-arg constructor needed for
         * {@link NativeMappedConverter#defaultValue()}
         */
        public SCARDHANDLE() {
            this(0l);
        }

        public SCARDHANDLE(long value) {
            super(value);
        }
    }

    /** PSCARDHANDLE used for SCardConnect. */
    public static class SCARDHANDLEByReference extends HandleByReference {

        public SCARDHANDLEByReference() {
            super();
        }

        public SCARDHANDLE getValue() {
            return new SCARDHANDLE(getLong());
        }

        public void setValue(SCARDHANDLE context) {
            setLong(context.longValue());
        }
    }

    public static class SCARD_IO_REQUEST extends Structure {

        public DWORD dwProtocol;
        public DWORD cbPciLength;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("dwProtocol", "cbPciLength");
        }
    }

}