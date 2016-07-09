package gdsmartcard.globalplatform;

public enum ISDTag {

    ISSUER_IDENTIFICATION_NUMBER(0x42), CARD_IMAGE_NUMBER(0x45), CARD_DATA(0x66), KEY_INFORMATION_TEMPLATE(0xE0);

    private int value;

    private ISDTag(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
