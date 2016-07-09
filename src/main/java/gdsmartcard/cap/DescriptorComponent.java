package gdsmartcard.cap;

/**
 * Represents the Descriptor component of the CAP-File. It is a derivation of the class Component.
 * 
 * @author stamers
 */
public final class DescriptorComponent extends Component
{
    /**
     * Constructs an DescriptorComponent object with the given byte array.
     * 
     * @param component data byte array of the Descriptor component.
     */
    DescriptorComponent(byte[] component)
    {
        super(component);
    }
}
