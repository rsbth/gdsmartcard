package gdsmartcard.io.jscard;

import java.nio.ByteBuffer;

import gdsmartcard.io.winscard.WinSCardDefines;

public class ReaderGroup {

    private ByteBuffer name;

    public ReaderGroup(String name) {
        this.name = (ByteBuffer) ByteBuffer.allocate(name.length() + 2).put(name.getBytes()).rewind();
    }

    public ReaderGroup(ByteBuffer name) {
        this.name = name;
    }

    public static final ReaderGroup ALL_READERS     = new ReaderGroup(WinSCardDefines.SCARD_ALL_READERS);
    public static final ReaderGroup DEFAULT_READERS = new ReaderGroup(WinSCardDefines.SCARD_DEFAULT_READERS);

    public ByteBuffer getName() {
        return name;
    }
}
