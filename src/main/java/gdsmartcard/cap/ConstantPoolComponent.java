package gdsmartcard.cap;

/**
 * Represents the Constant pool component of the CAP-File. It is a derivation of the class
 * Component.
 * 
 * @author stamers
 */
public final class ConstantPoolComponent extends Component
{
    
    /**
     * Constructs an ConstantPoolComponent object with the given byte array.
     * 
     * @param component data byte array of the Constant pool component.
     */
    ConstantPoolComponent(byte[] component)
    {
        super(component);
    }
}
