package gdsmartcard.cap;

/**
 * Represents the Class component of the CAP-File. It is a derivation of the class Component.
 * 
 * @author stamers
 */
public final class ClassComponent extends Component
{
    
    /**
     * Constructs an ClassComponent object with the given byte array.
     * 
     * @param component data byte array of the Class component.
     */
    ClassComponent(byte[] component)
    {
        super(component);
    }
}
