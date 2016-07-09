package gdsmartcard.cap;

/**
 * Represents the Static Field component of the CAP-File. It is a derivation of the class Component.
 * 
 * @author stamers
 */
public final class StaticFieldComponent extends Component
{
    
    /**
     * Constructs an StaticFieldComponent object with the given byte array.
     * 
     * @param component data byte array of the Static Field component.
     */
    StaticFieldComponent(byte[] component)
    {
        super(component);
    }
}
