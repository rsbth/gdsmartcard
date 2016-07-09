package gdsmartcard.globalplatform;

public enum ISDLifeCycleState {

    OP_READY(0x01), INITIALIZED(0x07), SECURED(0x0F), CARD_LOCKED(0x7F), TERMINATED(0xFF);

    private int value;

    private ISDLifeCycleState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
