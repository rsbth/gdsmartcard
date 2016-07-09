package gdsmartcard.cap;

/**
 * Represents the Method component of the CAP-File. It is a derivation of the class Component.
 * 
 * @author stamers
 */
public final class MethodComponent extends Component
{
    /**
     * Constructs an MethodComponent object with the given byte array.
     * 
     * @param component data byte array of the Method component.
     */
    MethodComponent(byte[] component)
    {
        super(component);
    }
}
