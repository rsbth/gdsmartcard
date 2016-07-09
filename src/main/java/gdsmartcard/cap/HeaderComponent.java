
package gdsmartcard.cap;

import static gdsmartcard.cap.Version.JAVA_CARD_2_2;

import java.io.IOException;
import java.util.Arrays;

import gdsmartcard.AID;

/**
 * Represents the Header component of the CAP-File. It is a derivation of the class Component.
 * 
 * @author stamers
 */
public final class HeaderComponent extends Component {

    /** . */
    private final String name;

    /**
     * Constructs an headerComponent object with the given byte array.
     * 
     * @param component
     *            data byte array of the Header component.
     */
    HeaderComponent(byte[] component) {
        super(component);

        if (getInt(0) != 0xdecaffed)
            throw new ComponentCorruptException("header component's magic (0x" + toHexString(getInt(0), 8).toUpperCase() + ") is not 0xDECAFFED"); //$NON-NLS-1$ //$NON-NLS-2$

        if (getCAPFileVersion().isCompatibleWith(JAVA_CARD_2_2)) {
            int offset = 10 + data[9] - 1;
            byte backup = data[offset];

            data[offset] = 0;
            try {
                name = getUTF8(offset);
            } catch (IOException iox) {
                throw new ComponentCorruptException("header component's package name is corrupt", iox); //$NON-NLS-1$
            }
            data[offset] = backup;
        } else
            name = null;
    }

    /**
     * Returns the AID of the package.
     * 
     * @return package AID.
     */
    public final AID getAID() {
        return new AID(data, 10, data[9] & 0xff);
    }

    /**
     * Returns the version of the CAP-File
     * 
     * @return CAP version.
     */
    public final Version getCAPFileVersion() {
        return new Version(data, 4);
    }

    /**
     * Returns the package version.
     * 
     * @return package version.
     */
    public final Version getPackageVersion() {
        return new Version(data, 7);
    }

    /**
     * Returns the package name.
     * 
     * @return Package name.
     */
    public final String getPackageName() {
        return name;
    }

    /**
     * Returns a hexadecimal string representation of the specified integer argument with the exact specified number of
     * digits. The conversion is performed by {@link Integer#toHexString(int) Integer.toHexString()} and then extended
     * or truncated to the specified numer of digits. Sign is taken into consideration when extension is necessary.
     * Positive numbers (including zero) are padded with leading '{@code 0}'s, negative numbers are padded with leading
     * '{@code f}'s.
     *
     * @param number
     *            an integer to be converted to a string.
     * @param digits
     *            the number of digits of the resulting string. May not be negative.
     * @return the hexadecimal string representation of {@code number}. Never returns {@code null}.
     */
    public static String toHexString(int number, int digits) {
        char[] filler = new char[digits];
        StringBuilder result;

        Arrays.fill(filler, (number >= 0) ? '0' : 'f');
        result = new StringBuilder(new String(filler)).append(Integer.toHexString(number));

        return result.substring(result.length() - digits);
    }

}
