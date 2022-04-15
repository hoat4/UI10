package ui10.binding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ObservableList<E> extends List<E>, Observable<ListChange<E>> {

    // TODO ki kéne találni, hogy hogyan lehetne upper boundos E esetén
    //      is működővé tenni a subscribe-ot

    default void enumerateAndSubscribe(Consumer<ListChange<E>> consumer) {
        consumer.accept(new ListChange<>(0, List.of(), List.copyOf(this)));
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
            for (int i = evt.index(); i < evt.index() + evt.oldElements().size(); i++) {
                scopes.get(i).close();
            }
            scopes.subList(evt.index(), evt.index() + evt.oldElements().size()).clear();

            for (int i = evt.index(); i < evt.index() + evt.newElements().size(); i++) {
                Scope scope = new Scope();
                scopes.add(i, scope);
                consumer.accept(evt.newElements().get(i - evt.index()), scope);
            }
        });
    }

    default void setAll(List<E> elements) {
        clear();
        addAll(elements);
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
            change.oldElements().forEach(removeHandler);
            change.newElements().forEach(addHandler);
        };
    }

    static <E> ObservableList<E> constantEmpty() {
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
