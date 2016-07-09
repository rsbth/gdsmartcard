package gdsmartcard.cap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This enum represents the type of a component. The following types are available: - HEADER -
 * DIRECTORY - IMPORT - APPLET - CLASS - METHOD - STATIC FIELD - EXPORT - CONSTANT POOL - REFERENCE
 * LOCATION - DESCRIPTOR - DEBUG
 * 
 * @author stamers
 */
public enum ComponentType
{
    
    /**
     * Identifies the HEADER component of the CAP-FILE.
     */
    HEADER(1, "Header.cap"), //$NON-NLS-1$
    
    /**
     * Identifies the DIRECTORY component of the CAP-FILE.
     */
    DIRECTORY(2, "Directory.cap"), //$NON-NLS-1$
    
    /**
     * Identifies the IMPORT component of the CAP-FILE.
     */
    IMPORT(4, "Import.cap"), //$NON-NLS-1$
    
    /**
     * Identifies the APPLET component of the CAP-FILE.
     */
    APPLET(3, "Applet.cap"), //$NON-NLS-1$
    
    /**
     * Identifies the CLASS component of the CAP-FILE.
     */
    CLASS(6, "Class.cap"), //$NON-NLS-1$
    
    /**
     * Identifies the METHOD component of the CAP-FILE.
     */
    METHOD(7, "Method.cap"), //$NON-NLS-1$
    
    /**
     * Identifies the STATIC FIELD component of the CAP-FILE.
     */
    STATIC_FIELD(8, "StaticField.cap"), //$NON-NLS-1$
    
    /**
     * Identifies the EXPORT component of the CAP-FILE.
     */
    EXPORT(10, "Export.cap"), //$NON-NLS-1$
    
    /**
     * Identifies the CONSTANT POOL component of the CAP-FILE.
     */
    CONSTANT_POOL(5, "ConstantPool.cap"), //$NON-NLS-1$
    
    /**
     * Identifies the REFERENC LOCATION component of the CAP-FILE.
     */
    REFERENCE_LOCATION(9, "RefLocation.cap"), //$NON-NLS-1$
    
    /**
     * Identifies the DESCRIPTOR component of the CAP-FILE.
     */
    DESCRIPTOR(11, "Descriptor.cap"), //$NON-NLS-1$
    
    /**
     * Identifies the DEBUG component of the CAP-FILE.
     */
    DEBUG(12, "Debug.cap"); //$NON-NLS-1$
    
    /** Map for mapping from tag ID to component type. */
    private static final Map<Integer, ComponentType> TAG2TYPE;
    
    /** Map for mapping from file name to component type. */
    private static final Map<String, ComponentType>  NAME2TYPE;
    
    // initialization of constants TAG2TYPE, NAME2TYPE
    static
    {
        Map<Integer, ComponentType> t2t = new HashMap<Integer, ComponentType>();
        Map<String, ComponentType> n2t = new HashMap<String, ComponentType>();
        
        for (ComponentType ct : values())
        {
            Integer iTag = new Integer(ct.getTag());
            t2t.put(iTag, ct);
            n2t.put(ct.getFileName(), ct);
        }
        Integer iDebugTag = new Integer(0xDB);
        t2t.put(iDebugTag, DEBUG); // special entry for Java Card 2.1 debug component
        
        TAG2TYPE = Collections.unmodifiableMap(t2t);
        NAME2TYPE = Collections.unmodifiableMap(n2t);
    }
    
    /** Tag ID of this component type. */
    private final int                                tagID;
    
    /** File name of this component type. */
    private final String                             name;
    
    /**
     * Private constructor of a ComponentType object. Given tag and file name identifies the
     * component type.
     * 
     * @param tag Tag of the Component type
     * @param fileName Name of the ComponentType File.
     */
    private ComponentType(int tag, String fileName)
    {
        tagID = tag & 0xff;
        name = fileName;
    }
    
    /**
     * Returns the tag of the ComponentType tag.
     * 
     * @return ComponentType tag.
     */
    public final int getTag()
    {
        return tagID;
    }
    
    /**
     * Returns the ComponentType File name.
     * 
     * @return File name.
     */
    public final String getFileName()
    {
        return name;
    }
    
    /**
     * Returns the ComponentType object identified by the given tag.
     * 
     * @param tag Tag of a ComponentType object.
     * @return ComponentType
     */
    public static final ComponentType typeOf(int tag)
    {
        Integer iTag = new Integer(tag & 0xff);
        return TAG2TYPE.get(iTag);
    }
    
    /**
     * Returns the ComponentType object identified by the given File name.
     * 
     * @param fileName File name of a ComponentType object.
     * @return ComponentType
     */
    public static final ComponentType typeOf(String fileName)
    {
        return NAME2TYPE.get(fileName);
    }
    
    /**
     * <p>
     * Returns a byte sequence representation of the current state of this object. Implementations
     * of this interface should never return {@code null}.
     * </p>
     * <p>
     * This method is not allowed to modify the externally visible state of this object (calling
     * {@code toByteArray()} twice on the same object without modifying its externally visible state
     * inbetween has to return {@link Object#equals(Object) equal} byte arrays).
     * </p>
     * <p>
     * Each invocation of this method should return a new byte array since external modification of
     * the byte array cannot be prevented.
     * </p>
     * <p>
     * In case the current state of this object cannot be represented by a byte sequence, this
     * method should throw an {@link IllegalStateException IllegalStateException}.
     * </p>
     * 
     * @return a byte sequence representation of the current state of this object.
     */
    public byte[] toByteArray()
    {
        return new byte[]
        {(byte)tagID};
    }
    
    /**
     * <p>
     * Returns the length of the byte sequence representation of the current state of this object.
     * The value returned by {@code anobject.getLength()} must always be identical to the result of
     * calling {@code anobject.toByteArray().length}; the implementation may just be more efficient
     * than doing the latter.
     * </p>
     * <p>
     * In case the current state of this object cannot be represented by a byte sequence, this
     * method should throw an {@link IllegalStateException IllegalStateException}.
     * </p>
     * 
     * @return the length of the byte sequence representation of the current state of this object.
     */
    public int getLength()
    {
        return 1;
    }
}
