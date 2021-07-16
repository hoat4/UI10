package ui10.binding;

import ui10.node.Node;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public interface ObservableList<E> extends List<E> {

    // TODO ki kéne találni, hogy hogyan lehetne upper boundos E esetén
    //      is működővé tenni a subscribe-ot

    void subscribe(Consumer<ListChange<E>> consumer);

    default void enumerateAndSubscribe(Consumer<ListChange<E>> consumer) {
        consumer.accept(new ListChange.ListAdd<>(0, List.copyOf(this)));
        subscribe(consumer);
    }

    void unsubscribe(Consumer<ListChange<E>> listChangeConsumer);

    default StreamBinding<E> streamBinding() {
        return new StreamBinding<>(this);
    }

    static <E> ObservableList<E> ofConstantElement(E element) {
        return new ObservableListImpl<>(List.of(element));
    }

    static <E> Consumer<ListChange<E>> simpleListSubscriber(Consumer<E> addHandler, Consumer<E> removeHandler) {
        // TODO pattern switch deconstruction pattern-nel, majd ha végre eljutnak odáig

        return change -> {
            if (change instanceof ListChange.ListAdd<E> a)
                a.elements().forEach(addHandler);
            else if (change instanceof ListChange.ListRemove<E> r)
                r.elements().forEach(removeHandler);
            else
                throw new IllegalArgumentException(change.toString());
        };
    }

    static <E> ObservableList<E> of(ScalarProperty<E> p) {
        @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
        List<E> l = Arrays.asList(p.get());

        ObservableList<E> o = new ObservableListImpl<>(l);
        p.subscribe(c->o.set(0, c.newValue()));
        return o;
    }
}
