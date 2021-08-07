package ui10.binding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;

public class StreamBinding<E> {

    private final ObservableList<?> source;
    private Op<?, ?> firstOp;
    private Op<?, ?> lastOp;

    public StreamBinding(ObservableList<E> source) {
        this.source = source;
    }

    @SuppressWarnings({"RedundantCast", "unchecked"}) // RedundantCast suppression needed because IntelliJ bug
    private <R> StreamBinding<R> addOp(Op<E, R> op) {
        if (firstOp == null)
            firstOp = op;
        else
            ((Op<?, E>) lastOp).next = op;
        lastOp = op;
        return (StreamBinding<R>) this;
    }

    public <R> StreamBinding<R> map(Function<E, R> f) {
        return addOp(new MapOp<>(f));
    }

    public <R> StreamBinding<R> flatMapProp(Function<E, ObservableScalar<R>> f) {
        return addOp(new FlatMapPropOp<>(f));
    }

    public ObservableList<E> toList() {
        ObservableList<E> list = new ObservableListImpl<>();
        addOp(new Finish<>(list));
        source.enumerateAndSubscribe(this::sourceChange);
        return list;
    }

    // itt a result Binding legyen vagy ObservableScalar?
    public ObservableScalar<E> reduce(ObservableScalar<E> identity, BinaryOperator<E> op) {
        // TODO ezt lehetne optimálisabban is

        ObservableList<E> list = toList();
        ScalarProperty<E> p = ScalarProperty.create();
        identity.getAndSubscribe(id->p.set(list.stream().reduce(id, op)));
        list.subscribe(c->{
            p.set(list.stream().reduce(identity.get(), op));
        });
        return p;
    }

    @SuppressWarnings("unchecked")
    private <F> void sourceChange(ListChange<?> evt) {
        Op<F, ?> first = (Op<F, ?>) firstOp;

        for (int i = 0; i < evt.oldElements().size(); i++)
            first.add(evt.index() + i, (F) evt.oldElements().get(i));

        for (int i = 0; i < evt.newElements().size(); i++)
            first.add(evt.index() + i, (F) evt.newElements().get(i));
    }

    private static abstract class Op<S, T> {

        Op<T, ?> next;

        abstract void add(int index, S e);

        abstract void replace(int index, S from, S to);

        abstract void remove(int index, S e);
    }

    private static class MapOp<S, T> extends Op<S, T> {

        private final Function<S, T> f;

        public MapOp(Function<S, T> f) {
            this.f = f;
        }

        @Override
        public void add(int index, S e) {
            next.add(index, f.apply(e));
        }

        // TODO az biztos jó hogy replace-nél és remove-nál is f-fel keressük meg a régi elem mappelt értékét?

        @Override
        void replace(int index, S from, S to) {
            next.replace(index, f.apply(from), f.apply(to));
        }

        @Override
        public void remove(int index, S e) {
            next.remove(index, f.apply(e));
        }
    }

    private static class FlatMapPropOp<S, T> extends Op<S, T> {

        private final Function<S, ObservableScalar<T>> f;
        private final List<ObservableScalar<T>> observables = new ArrayList<>();

        public FlatMapPropOp(Function<S, ObservableScalar<T>> f) {
            this.f = f;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void add(int index, S e) {
            ObservableScalar<T> o = f.apply(e);
            observables.add(index, o);

            next.add(index, o.get());
            o.subscribe(new Subscriber<>(index, next));
        }

        @Override
        public void replace(int index, S from, S to) {
            ObservableScalar<T> oOld = observables.get(index);
            ObservableScalar<T> oNew = f.apply(to);

            next.replace(index, oOld.get(), oNew.get());
            Subscriber<T> s = new Subscriber<>(index, next);
            oOld.unsubscribe(s);
            oNew.subscribe(s);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void remove(int index, S e) {
            ObservableScalar<T> o = observables.get(index);
            next.remove(index, o.get());
            o.unsubscribe(new Subscriber<>(index, next));
        }

        private record Subscriber<T>(int index, Op<T, ?> next) implements Consumer<ChangeEvent<? extends T>> {
            @Override
            public void accept(ChangeEvent<? extends T> evt) {
                next.replace(index, evt.oldValue(), evt.newValue());
            }
        }
    }

    private static class Finish<E> extends Op<E, Void> {

        private final ObservableList<E> list;

        public Finish(ObservableList<E> list) {
            this.list = list;
        }

        @Override
        public void add(int index, E e) {
            list.add(index, e);
        }

        @Override
        void replace(int index, E from, E to) {
            E old = list.set(index, to);
            if (!Objects.equals(old, from))
                throw new IllegalStateException();
        }

        @Override
        public void remove(int index, E e) {
            Object o = list.remove(index);
            if (o != e) // itt inkább Objects.equals kéne, ld. megjegyzést MapOp::replace-nél
                throw new RuntimeException("removed object differs: expected " + e +
                        " at index " + index + ", but got " + o);
        }
    }

}
