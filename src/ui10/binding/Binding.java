package ui10.binding;

public interface Binding<T> extends ObservableScalar<T> {

    void refresh();
}
