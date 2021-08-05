package ui10.binding;

import ui10.nodes.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ObservableList<E> extends List<E>, Observable<ListChange<E>> {

    // TODO ki kéne találni, hogy hogyan lehetne upper boundos E esetén
    //      is működővé tenni a subscribe-ot

    default void enumerateAndSubscribe(Consumer<ListChange<E>> consumer) {
        consumer.accept(new ListChange.ListAdd<>(0, List.copyOf(this)));
        subscribe(consumer);
    }

    default void scopedEnumerateAndSubscribe(BiConsumer<E, Scope> consumer) {
        List<Scope> scopes = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            Scope s = new Scope();
            scopes.add(s);
            consumer.accept(get(i), s);
        }
        subscribe(evt -> {
            if (evt instanceof ListChange.ListAdd<E> a) {
                for (int i = a.index(); i < a.index() + a.elements().size(); i++) {
                    Scope scope = new Scope();
                    scopes.add(i, scope);
                    consumer.accept(a.elements().get(i - a.index()), scope);
                }
            } else if (evt instanceof ListChange.ListRemove<E> r) {
                for (int i = r.index(); i < r.index() + r.elements().size(); i++) {
                    scopes.get(i).close();
                }
                scopes.subList(r.index(), r.index() + r.elements().size()).clear();
            } else {
                throw new UnsupportedOperationException(evt.toString());
            }

        });
    }

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

    static ObservableList<Node> constantEmpty() {
        return new ObservableListImpl<>(); // TODO legyen konstans
    }

    static <E> ObservableList<E> of(ObservableScalar<E> p) {
        @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
        List<E> l = Arrays.asList(p.get());

        ObservableList<E> o = new ObservableListImpl<>(l);
        p.subscribe(c -> o.set(0, c.newValue()));
        return o;
    }
}
