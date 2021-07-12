package ui10.binding;

import java.util.List;

public interface ListChange<T> {
    record ListAdd<E>(int index, List<E> elements) implements ListChange<E> {
    }

    record ListRemove<E>(int index, List<E> elements) implements ListChange<E> {
    }
}
