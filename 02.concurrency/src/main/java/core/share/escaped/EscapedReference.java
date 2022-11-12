package core.share.escaped;

public class EscapedReference {
    private int value = 0;

    public EscapedReference(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void increment() {
        value += 1;
    }
}
