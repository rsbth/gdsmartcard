package gdsmartcard.io.winscard;

public class WinSCardException extends Exception {

    public WinSCardException(long status) {
        super(String.format("0x%08x", status));
    }

}
