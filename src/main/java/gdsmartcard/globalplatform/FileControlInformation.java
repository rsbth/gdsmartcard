package gdsmartcard.globalplatform;

public class FileControlInformation {

    private byte[] fciData;

    public FileControlInformation(byte[] fci) throws MalformedFCIException {
        this.fciData = fci;
    }

    public byte[] getBytes() {
        return fciData;
    }
}
