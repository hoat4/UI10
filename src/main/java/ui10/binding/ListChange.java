package ui10.binding;

import java.util.List;
import java.util.function.Function;

public record ListChange<E>(int index, List<E> oldElements, List<E> newElements) {

    public <E2> void applyOn(List<E2> list, Function<E, E2> function) {
        for (E old : oldElements)
            list.remove(index);

        int i = index;
        for (E e : newElements)
            list.add(i++, function.apply(e));
    }
}
