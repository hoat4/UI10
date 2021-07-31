package ui10.binding;

public interface PropertyTransformation<T> {

    ScalarProperty<Boolean> valid();

    T apply(T t, Scope scope);
}
