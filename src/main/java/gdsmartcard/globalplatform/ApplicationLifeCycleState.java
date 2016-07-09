package gdsmartcard.globalplatform;

public enum ApplicationLifeCycleState {

    INSTALLED(0x03), SELECTABLE(0x07), LOCKED(0x83);

    private int value;

    private ApplicationLifeCycleState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
