package gdsmartcard.cap;

/**
 * Represents the Import component of the CAP-File. It is a derivation of the class Component.
 * 
 * @author stamers
 */
public final class ImportComponent extends Component
{
    /**
     * Constructs an ImportComponent object with the given byte array.
     * 
     * @param component data byte array of the Import component.
     */
    ImportComponent(byte[] component)
    {
        super(component);
    }
}
