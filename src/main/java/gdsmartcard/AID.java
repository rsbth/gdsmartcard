package gdsmartcard;

import java.util.Arrays;

/**
 * This class encapsulates the Application Identifier (AID) associated with an Application or Executable Load File. An
 * AID is defined in ISO 7816-5 to be a sequence of bytes between 5 and 16 bytes in length.
 */
public class AID {

    public static final int MIN_LENGTH = 5;
    public static final int MAX_LENGTH = 16;

    private byte[] bytes;

    /**
     * Creates a new {@link AID} object from the bytes contained in the given array.
     * <p>
     * <b>NOTE:</b> The AID length is not checked against the definition in ISO 7816-5. Therefore, it is possible to
     * create an {@link AID} object with an invalid length.
     * 
     * @param bytes
     *            the bytes to be used to create the AID
     */
    public AID(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    public AID(int... bytes) {
        this.bytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            this.bytes[i] = (byte) bytes[i];
        }
    }

    public AID(byte[] bytes, int offset, int length) {
        this.bytes = Arrays.copyOfRange(bytes, offset, offset + length);
    }

    /**
     * Returns a byte array containing the bytes defining this {@link AID}.
     * 
     * @return a byte array containing the bytes defining this {@link AID}
     */
    public byte[] toBytes() {
        return bytes.clone();
    }

    /**
     * Tells whether this AID is valid or not.
     * 
     * @return <code>true</code> if this AID is valid, <code>false</code> otherwise
     */
    public boolean isValid() {
        return bytes.length >= MIN_LENGTH && bytes.length <= MAX_LENGTH;
    }
}
