package gdsmartcard.globalplatform;

public class DeleteConfirmation {

    private byte[] bytes;

    public DeleteConfirmation(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getLength() {
        return bytes.length;
    }

    public byte[] toBytes() {
        return bytes.clone();
    }

}
