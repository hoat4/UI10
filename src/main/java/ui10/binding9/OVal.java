package ui10.binding9;

import java.util.Objects;

public class OVal<T> extends Observable {
    
    private T value;

    public OVal() {
        this(null);
    }

    public OVal(T value) {
        validate(value);
        this.value = normalize(value);
    }

    public T get() {
        onRead();
        return value;
    }

    public void set(T value) {
        value = normalize(value);
        T prev = this.value;
        if (!Objects.equals(value, prev)) {
            this.value = value;
            onWrite();
            afterChange(prev, value);
        }
    }

    protected void validate(T value) {
    }

    protected T normalize(T value) {
        return value;
    }

    protected void afterChange(T oldValue, T newValue) {}
}
