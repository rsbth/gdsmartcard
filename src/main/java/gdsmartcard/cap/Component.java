package gdsmartcard.cap;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Represents a component of the CAP-File. It contains a specific identifier of the component and a
 * data byte array.
 * 
 * @author stamers
 */
public class Component
{
    
    /** The tag of this component. */
    byte   tag;
    
    /** Contains the data of this component. */
    byte[] data;
    
    /**
     * Constructs a Component object with the given byte array.
     * 
     * @param component data byte array of the component.
     */
    Component(byte[] component)
    {
        tag = component[0];
        data = Arrays.copyOfRange(component, 3, component.length);
        
        int size = ((component[1] << 8) | (component[2] & 0xff)) & 0xffff;
        
        if (size != data.length)
            throw new IllegalArgumentException(
                "component's size (" + size + ") differs from actual component size (" + data.length + ')'); //$NON-NLS-1$//$NON-NLS-2$
    }
    
    /**
     * Returns the type of the component.
     * 
     * @return component type as ComponentType object.
     */
    public final ComponentType getType()
    {
        return ComponentType.typeOf(tag);
    }
    
    /**
     * Returns the type of the component.
     * 
     * @return component type as byte array.
     */
    public final byte getTag()
    {
        return tag;
    }
    
    /**
     * Returns the length of the data of the component.
     * 
     * @return data length.
     */
    public final int getSize()
    {
        return data.length;
    }
    
    /** 
     * Returns the value at the given offset as unsigned byte value.
     * @param offset Offset of the requested value.
     * @return value at the given offset as unsigned byte.
     * */
    final int getUnsignedByte(int offset)
    {
        return data[offset] & 0xff;
    }
    
    /** 
     * Returns the value at the given offset as short value.
     * @param offset Offset of the requested value.
     * @return value at the given offset as short.
     * */
    final int getShort(int offset)
    {
        return (short)((data[offset] << 8) | (data[offset + 1] & 0x00ff));
    }
    
    /** 
     * Returns the value at the given offset as unsigned short value.
     * @param offset Offset of the requested value.
     * @return value at the given offset as unsigned short.
     * */
    final int getUnsignedShort(int offset)
    {
        return ((data[offset] << 8) | (data[offset + 1] & 0x00ff)) & 0xffff;
    }
    
    /** 
     * Returns the value at the given offset as int value.
     * @param offset Offset of the requested value.
     * @return value at the given offset as int.
     * */
    final int getInt(int offset)
    {
        return (((data[offset] << 24) & 0xff000000) | ((data[offset + 1] << 16) & 0x00ff0000) |
                ((data[offset + 2] << 8) & 0x0000ff00) | (data[offset + 3] & 0x000000ff));
    }
    

    
    /** 
     * Returns the value at the given offset as String value.
     * @param offset Offset of the requested value.
     * @return value at the given offset as String.
     * @throws IOException 
     * */
    final String getUTF8(int offset) throws IOException
    {
        String result = new DataInputStream(new ByteArrayInputStream(data, offset,
            getUnsignedShort(offset) + 2)).readUTF();
        
        return ((result != null) && result.length() != 0) ? result : null;
    }

      /** <p>
       *  Returns a byte sequence representation of the current state of this
       *  object. Implementations of this interface should never return {@code
       *  null}.
       *  </p><p>
       *  This method is not allowed to modify the externally visible state of this
       *  object (calling {@code toByteArray()} twice on the same object without
       *  modifying its externally visible state inbetween has to return {@link
       *  Object#equals(Object) equal} byte arrays).
       *  </p><p>
       *  Each invocation of this method should return a new byte array since
       *  external modification of the byte array cannot be prevented.
       *  </p><p>
       *  In case the current state of this object cannot be represented by a byte
       *  sequence, this method should throw an {@link IllegalStateException
       *  IllegalStateException}.
       *  </p>
       *
       *  @return a byte sequence representation of the current state of this
       *          object.
       */
    public final byte[] toByteArray()
    {
        int size = data.length;
        byte[] result = new byte[size + 3];
        
        result[0] = tag;
        result[1] = (byte)(size >> 8);
        result[2] = (byte)size;
        System.arraycopy(data, 0, result, 3, size);
        
        return result;
    }
    
      /** <p>
       *  Returns the length of the byte sequence representation of the current
       *  state of this object. The value returned by {@code anobject.getLength()}
       *  must always be identical to the result of calling {@code
       *  anobject.toByteArray().length}; the implementation may just be more
       *  efficient than doing the latter.
       *  </p><p>
       *  In case the current state of this object cannot be represented by a byte
       *  sequence, this method should throw an {@link IllegalStateException
       *  IllegalStateException}.
       *  </p>
       *
       *  @return the length of the byte sequence representation of the current
       *          state of this object.
       */
    public final int getLength()
    {
        return data.length + 3;
    }
    
    /** . 
     * Creates a Component object from the given byte array.
     * @param component byte array of the component.
     * @return Component object.
     * */
    public static final Component createFrom(byte[] component)
    {
        Component result = null;
        
        switch (ComponentType.typeOf(component[0]))
        {
            case HEADER:
                result = new HeaderComponent(component);
                break;
            
            case DIRECTORY:
                result = new DirectoryComponent(component);
                break;
            
            case IMPORT:
                result = new ImportComponent(component);
                break;
            
            case APPLET:
                result = new AppletComponent(component);
                break;
            
            case CLASS:
                result = new ClassComponent(component);
                break;
            
            case METHOD:
                result = new MethodComponent(component);
                break;
            
            case STATIC_FIELD:
                result = new StaticFieldComponent(component);
                break;
            
            case EXPORT:
                result = new ExportComponent(component);
                break;
            
            case CONSTANT_POOL:
                result = new ConstantPoolComponent(component);
                break;
            
            case REFERENCE_LOCATION:
                result = new RefLocationComponent(component);
                break;
            
            case DESCRIPTOR:
                result = new DescriptorComponent(component);
                break;
            
            case DEBUG:
                result = new DebugComponent(component);
                break;
            
            default:
                assert false : "unknow component type: " + ComponentType.typeOf(component[0]); //$NON-NLS-1$
        }
        
        return result;
    }
}
