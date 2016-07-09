package gdsmartcard.cap;

/**
 * Represents the ReferenceLocation component of the CAP-File. It is a derivation of the class
 * Component.
 * 
 * @author stamers
 */
public final class RefLocationComponent extends Component
{
    /**
     * Constructs an ReferenceLocationComponent object with the given byte array.
     * 
     * @param component data byte array of the ReferenceLocation component.
     */
    RefLocationComponent(byte[] component)
    {
        super(component);
    }
}
