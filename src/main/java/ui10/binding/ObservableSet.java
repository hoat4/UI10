package ui10.binding;

import java.util.List;
import java.util.Set;

public interface ObservableSet<E> extends Set<E>, Observable<ObservableSet.SetChange<E>> {

    interface SetChange<T> {

        record SetAdd<E>(Set<E> elements) implements SetChange<E> {
        }

        record SetRemove<E>(List<E> elements) implements SetChange<E> {
        }
    }
}
