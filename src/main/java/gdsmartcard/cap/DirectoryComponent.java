package gdsmartcard.cap;

/**
 * Represents the Directory component of the CAP-File. It is a derivation of the class Component.
 * 
 * @author stamers
 */
public final class DirectoryComponent extends Component
{
    
    /**
     * Constructs an DirectoryComponent object with the given byte array.
     * 
     * @param component data byte array of the Directory component.
     */
    DirectoryComponent(byte[] component)
    {
        super(component);
    }
}
