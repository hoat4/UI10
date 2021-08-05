package ui10.binding;

import java.util.*;
import java.util.function.Consumer;

public class SingleElementObservableList<E> extends AbstractList<E> implements ObservableList<E> {

    private ObservableScalar<E> o;

    public SingleElementObservableList(ObservableScalar<E> o) {
        this.o = o;
        // ez így nem jó, mert nincs írási garancia final híján
    }

    public void init(ObservableScalar<E> o) {
        if (this.o == null)
            this.o = o;
        else
            throw new IllegalStateException();
    }

    @Override
    public void subscribe(Consumer<? super ListChange<E>> consumer) {
        o.subscribe(new SubscriberWrapper<>(consumer));
    }

    @Override
    public void unsubscribe(Consumer<? super ListChange<E>> listChangeConsumer) {
        o.unsubscribe(new SubscriberWrapper<>(listChangeConsumer));
    }

    private static record SubscriberWrapper<E>(Consumer<? super ListChange<E>> consumer) implements Consumer<ChangeEvent<E>> {

        @Override
        public void accept(ChangeEvent<E> c) {
            if (c.oldValue() != null)
                consumer.accept(new ListChange.ListRemove<>(0, List.of(c.oldValue())));
            if (c.newValue() != null)
                consumer.accept(new ListChange.ListAdd<>(0, List.of(c.newValue())));
        }
    }

    @Override
    public int size() {
        return o.get() == null ? 0 : 1;
    }

    @Override
    public E get(int index) {
        E e;
        if (index != 0 || (e = o.get()) == null)
            throw new IndexOutOfBoundsException();
        return e;
    }
}
