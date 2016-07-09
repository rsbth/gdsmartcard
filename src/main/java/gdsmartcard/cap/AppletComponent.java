package gdsmartcard.cap;

import gdsmartcard.AID;

/**
 * Represents the Applet component of the CAP-File. It is a derivation of the class Component.
 * 
 * @author stamers
 */
public final class AppletComponent extends Component {

    /**
     * Constructs an AppletComponent object with the given byte array.
     * 
     * @param component
     *            data byte array of the Applet component.
     */
    AppletComponent(byte[] component) {
        super(component);
    }

    /**
     * Returns the AIDs of the Applet classes of the CAP-File.
     * 
     * @return array of all AIDs of the CAP-File.
     */
    public AID[] getAppletClasses() {
        AID[] result = new AID[getUnsignedByte(0)];
        int off = 1;

        for (int i = 0; i < result.length; i++) {
            result[i] = new AID(data, off + 1, data[off] & 0xff);
            off += getUnsignedByte(off) + 3;
        }

        return result;
    }
}
