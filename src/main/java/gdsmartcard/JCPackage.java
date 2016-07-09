package gdsmartcard;

import static gdsmartcard.cap.ComponentType.APPLET;
import static gdsmartcard.cap.ComponentType.DEBUG;
import static gdsmartcard.cap.ComponentType.HEADER;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;

import gdsmartcard.cap.AppletComponent;
import gdsmartcard.cap.Component;
import gdsmartcard.cap.ComponentType;
import gdsmartcard.cap.DebugComponent;
import gdsmartcard.cap.HeaderComponent;
import gdsmartcard.cap.Version;
import gdsmartcard.globalplatform.GlobalPlatform;

/**
 * Represents a CAP-File. Offers a method to return the CAP-File as byte array.
 * 
 * @author stamers
 */
public class JCPackage {

    private static final int LOAD_BLOCK_LENGTH = 0xE0;

    /** The fully qualified name of this package. */
    private String name;

    /** Contains all components of this package. */
    private final Map<ComponentType, Component> componentMap;

    /**
     * Flag that indicates whether the byte sequence representation of this package includes the descriptor component
     * (if existing).
     */
    private boolean doIncludeDescriptor;

    /**
     * Flag that indicates whether the byte sequence representation of this package includes the debug component (if
     * existing).
     */
    private boolean doIncludeDebug;

    /**
     * Flag that indicates whether the byte sequence representation of this package includes a directory component
     * adjusted to not refer to the descriptor component or the debug component (if existing but not part of the byte
     * sequence representation of this package).
     */
    private boolean doAdjustDirectory;

    /** . */
    private JCPackage() {
        name = null;
        componentMap = new EnumMap<ComponentType, Component>(ComponentType.class);
        doIncludeDescriptor = false;
        doIncludeDebug = false;
        doAdjustDirectory = false;
    }

    /**
     * Returns the package name.
     * 
     * @return Package name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the AID of the package.
     * 
     * @return package AID.
     */
    public AID getAID() {
        return ((HeaderComponent) (componentMap.get(HEADER))).getAID();
    }

    /**
     * Returns the AID of the package.
     * 
     * @return package AID.
     */
    public AID[] getClassAID() {
        AppletComponent oAppletComp = (AppletComponent) componentMap.get(APPLET);

        if (oAppletComp != null)
            return oAppletComp.getAppletClasses();

        return new AID[0];
    }

    /**
     * Return the flag that indicates whether the byte sequence representation of this package includes the descriptor
     * component (if existing)
     * 
     * @return Descriptor flag
     */
    public boolean getIncludeDescriptor() {
        return doIncludeDescriptor;
    }

    /**
     * Sets the flag that indicates whether the byte sequence representation of this package includes the descriptor
     * component (if existing)
     * 
     * @param includeDescriptor
     *            Descriptor flag
     */
    public void setIncludeDescriptor(boolean includeDescriptor) {
        this.doIncludeDescriptor = includeDescriptor;
    }

    /**
     * Sets the flag that indicates whether the byte sequence representation of this package includes the debug
     * component (if existing).
     * 
     * @return Debug flag.
     */
    public boolean getIncludeDebug() {
        return doIncludeDebug;
    }

    /**
     * Returns the flag that indicates whether the byte sequence representation of this package includes the debug
     * component (if existing).
     * 
     * @param includeDebug
     *            Debug flag.
     */
    public void setIncludeDebug(boolean includeDebug) {
        this.doIncludeDebug = includeDebug;
    }

    /**
     * Returns the flag that indicates whether the byte sequence representation of this package includes a directory
     * component adjusted to not refer to the descriptor component or the debug component (if existing but not part of
     * the byte sequence representation of this package).
     * 
     * @return Directory flag.
     */
    public boolean getAdjustDirectory() {
        return doAdjustDirectory;
    }

    /**
     * Returns the flag that indicates whether the byte sequence representation of this package includes a directory
     * component adjusted to not refer to the descriptor component or the debug component (if existing but not part of
     * the byte sequence representation of this package).
     * 
     * @param adjustDirectory
     *            Directory flag.
     */
    public void setAdjustDirectory(boolean adjustDirectory) {
        this.doAdjustDirectory = adjustDirectory;
    }

    /**
     * Returns the version of the CAP-File
     * 
     * @return CAP version.
     */
    public Version getCAPFileVersion() {
        return ((HeaderComponent) (componentMap.get(HEADER))).getCAPFileVersion();
    }

    /**
     * Returns the package version.
     * 
     * @return package version.
     */
    public Version getPackageVersion() {
        return ((HeaderComponent) (componentMap.get(HEADER))).getPackageVersion();
    }

    /**
     * Returns a byte sequence representation of the current state of this object. Implementations of this interface
     * should never return {@code null}. This method is not allowed to modify the externally visible state of this
     * object (calling {@code toByteArray()} twice on the same object without modifying its externally visible state
     * inbetween has to return {@link Object#equals(Object) equal} byte arrays). Each invocation of this method should
     * return a new byte array since external modification of the byte array cannot be prevented. In case the current
     * state of this object cannot be represented by a byte sequence, this method should throw an
     * {@link IllegalStateException IllegalStateException}.
     * 
     * @return a byte sequence representation of the current state of this object.
     */
    public byte[] toByteArray() {
        byte[] result = new byte[getLength()];
        byte[] data;
        int off = 0;
        int len;

        for (Component comp : componentMap.values()) {
            switch (comp.getType()) {
            case HEADER:
            case IMPORT:
            case APPLET:
            case CLASS:
            case METHOD:
            case STATIC_FIELD:
            case EXPORT:
            case CONSTANT_POOL:
            case REFERENCE_LOCATION:
                data = comp.toByteArray();
                len = data.length;
                System.arraycopy(data, 0, result, off, len);
                off += len;
                break;

            case DIRECTORY:
                if (doAdjustDirectory) {
                    // TODO
                } else {
                    data = comp.toByteArray();
                    len = data.length;
                    System.arraycopy(data, 0, result, off, len);
                    off += len;
                }
                break;

            case DESCRIPTOR:
                if (doIncludeDescriptor) {
                    data = comp.toByteArray();
                    len = data.length;
                    System.arraycopy(data, 0, result, off, len);
                    off += len;
                }
                break;

            case DEBUG:
                if (doIncludeDebug) {
                    data = comp.toByteArray();
                    len = data.length;
                    System.arraycopy(data, 0, result, off, len);
                    off += len;
                }
                break;
            }
        }

        return result;
    }

    /**
     * Returns the length of the byte sequence representation of the current state of this object. The value returned by
     * {@code anobject.getLength()} must always be identical to the result of calling
     * {@code anobject.toByteArray().length}; the implementation may just be more efficient than doing the latter. In
     * case the current state of this object cannot be represented by a byte sequence, this method should throw an
     * {@link IllegalStateException IllegalStateException}.
     * 
     * @return the length of the byte sequence representation of the current state of this object.
     */
    public int getLength() {
        int result = 0;

        for (Component comp : componentMap.values()) {
            switch (comp.getType()) {
            case HEADER:
            case IMPORT:
            case APPLET:
            case CLASS:
            case METHOD:
            case STATIC_FIELD:
            case EXPORT:
            case CONSTANT_POOL:
            case REFERENCE_LOCATION:
                result += comp.getSize() + 3;
                break;

            case DIRECTORY:
                if (doAdjustDirectory) {
                    // TODO
                } else
                    result += comp.getSize() + 3;
                break;

            case DESCRIPTOR:
                if (doIncludeDescriptor)
                    result += comp.getSize() + 3;
                break;

            case DEBUG:
                if (doIncludeDebug)
                    result += comp.getSize() + 3;
                break;
            }
        }

        return result;
    }

    public void download(CardChannel channel) throws CardException {
        // send the INSTALL [for load] command
        ChannelHelper c = new ChannelHelper(channel);
        CommandAPDU installForLoadCommand = GlobalPlatform.installForLoad(getAID());
        c.transmit(installForLoadCommand);

        // load the CAP file
        ByteBuffer loadDataBuffer = toLoadFile();
        byte[] nextLoadBlock = new byte[LOAD_BLOCK_LENGTH];

        int blockNumber = 0;
        boolean isLastBlock = false;
        while (loadDataBuffer.hasRemaining()) {
            if (loadDataBuffer.remaining() <= LOAD_BLOCK_LENGTH) {
                // last load block
                nextLoadBlock = new byte[loadDataBuffer.remaining()];
                isLastBlock = true;
            }
            loadDataBuffer.get(nextLoadBlock);
            c.transmit(GlobalPlatform.loadCommand(nextLoadBlock, blockNumber++, isLastBlock));
        }
    }

    /**
     * Returns a {@link ByteBuffer} containing the CAP file contents in Load File format.
     * 
     * @return a {@link ByteBuffer} containing the CAP file contents in Load File format
     */
    public ByteBuffer toLoadFile() {
        byte[] loadFileContent = toByteArray();

        // the length has to be BER encoded
        byte[] lengthBytes = new BigInteger(String.format("%d", loadFileContent.length)).toByteArray();
        if (lengthBytes.length == 1) {
            // one byte length: nothing more to encode
        } else if (lengthBytes[0] != 0) {
            // all bytes are used to encode length: we have to add a leading byte to encode
            // the number of length bytes
            lengthBytes = ByteBuffer.allocate(lengthBytes.length + 1).put((byte) (0x80 + lengthBytes.length)).put(lengthBytes).array();
        } else {
            // there is already a leading zero that we can use for encoding
            lengthBytes[0] = (byte) (0x80 + lengthBytes.length - 1);
        }

        return ByteBuffer.allocate(1 + lengthBytes.length + loadFileContent.length)
                .put((byte) 0xC4)
                .put(lengthBytes)
                .put(loadFileContent);
    }

    @Override
    public String toString() {
        StringBuilder oAIDString = new StringBuilder();

        if (name != null) {
            byte[] abyAID = getAID().toBytes();
            oAIDString.append(name);
            for (int i = 0; i < abyAID.length; i++) {
                oAIDString.append(String.format("%x2", new Byte(abyAID[i]))); //$NON-NLS-1$
            }

            oAIDString.append(getPackageVersion().toString());
        }

        return oAIDString.toString();
    }

    /**
     * This method creates a JCPackage object with the given CAP-File and returns this object.
     * 
     * @param capFile
     *            CAP- File as String.
     * @return JCPackage object.
     * @throws IOException
     */
    public static final JCPackage fromCAP(String capFile) throws IOException {
        return createFromCAPFile(new File(capFile));
    }

    /**
     * This method creates a JCPackage object with the given CAP-File and returns this object.
     * 
     * @param capFile
     *            CAP- File as File object.
     * @return JCPackage object.
     * @throws IOException
     */
    public static final JCPackage createFromCAPFile(File capFile) throws IOException {
        if (capFile.isDirectory())
            return createFromCAPFile(new File(new File(capFile, "javacard"), //$NON-NLS-1$
                    capFile.getCanonicalFile().getName() + ".cap")); //$NON-NLS-1$

        JCPackage result = new JCPackage();
        JarFile jarFile = new JarFile(capFile);
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        String fullName;
        String compName;
        ComponentType type;

        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();

            if (!jarEntry.isDirectory()) {
                fullName = jarEntry.getName();
                compName = (fullName.lastIndexOf('/') > 0)
                        ? fullName.substring(fullName.lastIndexOf('/') + 1) : fullName;
                if ((type = ComponentType.typeOf(compName)) != null)
                    result.componentMap.put(type, loadComponent(jarFile, jarEntry));
                // if (type == HEADER) -> store fullName for later package name retrieval
            }
        }

        // package name available in header component ?
        if ((result.name = ((HeaderComponent) (result.componentMap.get(HEADER))).getPackageName()) == null) {
            // no -> package name available in debug component ?
            DebugComponent debug = ((DebugComponent) (result.componentMap.get(DEBUG)));

            if (debug != null)
                result.name = debug.getPackageName();

            // no -> package name available in manifest ?
            if (result.name == null) {
                // TODO set package name from Manifest/?fullName?
            }
        }

        return result;
    }

    /**
     * This method loads from the given JAR file a component of the CAP-File from the given JAR offset.
     * 
     * @param jarFile
     *            JAR-File of the CAP-File
     * @param jarEntry
     *            Entry position of the JAR-File.
     * @return Component object of the CAP-File.
     * @throws IOException
     */
    private static Component loadComponent(JarFile jarFile, JarEntry jarEntry) throws IOException {
        byte[] buffer = new byte[(int) jarEntry.getSize()];
        InputStream in = jarFile.getInputStream(jarEntry);
        int off = 0;
        int len = buffer.length;

        try {
            while (off < len)
                off += in.read(buffer, off, len - off);
        } finally {
            in.close();
        }

        return Component.createFrom(buffer);
    }
}
