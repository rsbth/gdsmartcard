
package gdsmartcard.cap;



/**
 * Represents the CAP-File and package version. 
 * @author stamers
 *
 */
public final class Version
  implements Comparable<Version>
{


  /** . */
  public static final Version JAVA_CARD_2_1 = new Version(2, 1);

  /** . */
  public static final Version JAVA_CARD_2_2 = new Version(2, 2);



  /** Major version component of this version. */
  private int majorVer;

  /** Minor version component of this version. */
  private int minorVer;



  /**
   * Constructs a Version object with the given Major and Minor version component.
 * @param major Major version component of the version
 * @param minor Minor version component of the version
   */
  public Version ( int major, int minor )
  {
    if ((major < Byte.MIN_VALUE) || (major > 255))
      throw new IllegalArgumentException("value " + major + " too large for major version (has more than 8 significant bits)"); //$NON-NLS-1$ //$NON-NLS-2$
    if ((minor < Byte.MIN_VALUE) || (minor > 255))
      throw new IllegalArgumentException("value " + minor + " too large for minor version (has more than 8 significant bits)");  //$NON-NLS-1$//$NON-NLS-2$

    majorVer = major;
    minorVer = minor;
  }


  /**
   * Constructs a Version object with the given bytes.
   * @param versionBytes Version byte array.
   */
  public Version ( byte[] versionBytes )
  {
      this(versionBytes[1], versionBytes[0]);
      
      StringBuilder oVersionBytesString = new StringBuilder();

      for(int i = 0; i < versionBytes.length; i++)
      {
          oVersionBytesString.append(String.format("%x2", new Byte(versionBytes[i]))); //$NON-NLS-1$
      }

    if (versionBytes.length > 2)
      throw new IllegalArgumentException(oVersionBytesString.toString() + " is too large (must have exactly 2 bytes)"); //$NON-NLS-1$
  }


  /**
   * Constructs a Version object with the given bytes from the given offset.
   * @param versionBytes Byte array contains the version bytes.
 * @param offset At this offset starts the version bytes.
   */
  public Version ( byte[] versionBytes, int offset )
  {
    this(versionBytes[offset+1], versionBytes[offset]);
  }

  /**
   * Returns the Major version component of this version.
   * @return Major version component.
   */
  public int getMajor ( )
  {
    return majorVer;
  }

  /**
   * Returns the Minor version component of this version.
   * @return Minor version component.
   */
  public int getMinor ( )
  {
    return minorVer;
  }


  /**
   * Checks, if the given Version is compatible with this version.
   * @param version Version object.
   * @return True, if the two versions are compatible. False, if not.
   */
  public boolean isCompatibleWith ( Version version )
  {
    return (majorVer == version.majorVer) && (minorVer >= version.minorVer);
  }


  /** <p>
   *  Returns a byte sequence representation of the current state of this
   *  data unit. Subclasses are not allowed to return {@code null}.
   *  </p><p>
   *  This method is not allowed to modify the externally visible state of this
   *  data unit (calling {@code toByteArray()} twice on the same data unit
   *  without modifying its externally visible state inbetween has to return
   *  {@link Object#equals(Object) equal} byte arrays).
   *  </p><p>
   *  Each invocation of this method should return a new byte array (even if
   *  this data unit is immutable) since external modification of the byte
   *  array cannot be prevented.
   *  </p><p>
   *  In case the current state of this data unit cannot be represented by a
   *  byte sequence, this method should throw an {@link IllegalStateException
   *  IllegalStateException}.
   *  </p>
   *
   *  @return a byte sequence representation of the current state of this data
   *          unit.
   */
  public byte[] toByteArray ( )
  {
    return new byte[] { (byte)minorVer, (byte)majorVer };
  }


  /** <p>
   *  Returns the length of the byte sequence representation of the current
   *  state of this data unit. The value returned by {@code
   *  someVersion.getLength()} must always be identical to the result of
   *  calling {@code someVersion.toByteArray().length}; the implementation may
   *  just be more efficient than doing the latter.
   *  </p><p>
   *  In case the current state of this data unit cannot be represented by a
   *  byte sequence, this method should throw an {@link IllegalStateException
   *  IllegalStateException}.
   *  </p>
   *
   *  @return the length of the byte sequence representation of the current
   *          state of this data unit.
   */
  public int getLength ( )
  {
    return 2;
  }



  /** . */
  @Override
  public int compareTo ( Version ver )
  {
    int result;

    return (((result = majorVer - ver.majorVer) != 0) ? result : minorVer - ver.minorVer);
  }


  /** Indicates whether some other object is "equal to" this data unit.
   *
   *  @param obj the reference object with which to compare. May be {@code
   *             null}.
   *  @return    {@code true} if the specified object is of the exact same type
   *             as this data unit and both data units have identical
   *             {@linkplain #toByteArray() byte sequence representations};
   *             otherwise {@code false} is returned.
   */
  @Override
  public boolean equals ( Object obj )
  {
    return (    (obj instanceof Version)
             && (majorVer == ((Version)obj).majorVer)
             && (minorVer == ((Version)obj).minorVer)
           );
  }


  /** Returns a string representation of this data unit. The default
   *  implementation of class {@code Version} returns the {@linkplain
   *  Class#getSimpleName() simple class name} of this data unit followed by
   *  the  string representation} of this
   *  data unit's {@linkplain #toByteArray() byte sequence representation}.
   *
   *  @return a string representation of this data unit. Never returns {@code
   *          null}.
   */
  @Override
  public String toString ( )
  {
    return new StringBuilder().append('{').append(majorVer).append('.').append(minorVer).append('}').toString();
  }
}
