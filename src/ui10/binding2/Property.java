package ui10.binding2;

public class Property<T> {

    public final T defaultValue;
    public final boolean inheritable;

    public Property() {
        this.defaultValue = null;
        this.inheritable = true;
    }

    public Property(T defaultValue) {
        this.defaultValue = defaultValue;
        this.inheritable = true;
    }

    public Property(T defaultValue, boolean inheritable) {
        this.defaultValue = defaultValue;
        this.inheritable = inheritable;
    }
}
