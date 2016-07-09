
package gdsmartcard.cap;

import java.io.IOException;


/**
 * Represents the Debug component of the CAP-File. It is a derivation of the class Component.
 * 
 * @author stamers
 */
public final class DebugComponent extends Component
{


  /** . */
  private final String[] strings;

  /** . */
  private final String packageName;


  /**
   * Constructs an DebugComponent object with the given byte array.
   * 
   * @param component data byte array of the  Debug component.
   */
  DebugComponent ( byte[] component )
  {
    super(component);

    int offset = 0;

    // check if 
    if (component[0] == (byte) 0xDB)
        offset = 12;
    
    try
    {
      strings = new String[getUnsignedShort(offset)];
      offset += 2;
      
      for (int i = 0; i < strings.length; i++)
      {
        strings[i] = getUTF8(offset);
        offset    += getUnsignedShort(offset) + 2;
      }
    }
    catch ( IOException iox )
    {
      throw new ComponentCorruptException("debug component's string table is corrupt", iox); //$NON-NLS-1$
    }

    packageName = strings[getUnsignedShort(offset)];
  }



  /**
   * Returns the package name.
   * @return Package name
   */
  public final String getPackageName ( )
  {
    return packageName;
  }
}
