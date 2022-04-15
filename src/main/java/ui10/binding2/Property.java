package ui10.binding2;

public class Property<T> {

    public final T defaultValue;
    public final boolean inheritable;

    public Property(boolean inheritable) {
        this(inheritable, null);
    }

    public Property(boolean inheritable, T defaultValue) {
        this.defaultValue = defaultValue;
        this.inheritable = inheritable;
    }

}
