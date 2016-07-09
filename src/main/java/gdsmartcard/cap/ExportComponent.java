package gdsmartcard.cap;

/**
 * Represents the Export component of the CAP-File. It is a derivation of the class Component.
 * 
 * @author stamers
 */
public final class ExportComponent extends Component
{
    
    /**
     * Constructs an ExportComponent object with the given byte array.
     * 
     * @param component data byte array of the Export component.
     */
    ExportComponent(byte[] component)
    {
        super(component);
    }
}
