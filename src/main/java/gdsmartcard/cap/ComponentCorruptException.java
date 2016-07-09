

package gdsmartcard.cap;



/** Non-public special runtime exception that is used to indicate a test
 *  element step has failed when executed under timeout, either by returning
 *  {@code false} or by throwing an exception or an assertion error. Its
 *  purpose is to transport the information that a method has failed from its
 *  executing thread to the main thread through an .
 *
 *  @version 1.0
 */
public final class ComponentCorruptException extends RuntimeException
{

  /** UID for serialization. */
  static final long serialVersionUID = 7143033817620813996L;


  /** Non-public constructor with message. Constructs a new component corrupt
   *  exception with the given detail message and no cause.
 * @param message 
   */
  ComponentCorruptException ( String message )
  {
    super(message);
  }


  /** Non-public constructor with message and cause. Constructs a new component
   *  corrupt exception with the given detail message and cause.
 * @param message 
 * @param cause 
   */
  ComponentCorruptException ( String message, Throwable cause )
  {
    super(message, cause);
  }
}
