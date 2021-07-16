package ui10.binding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class StreamBinding<E> {

    private final ObservableList<?> source;
    private final List<Op<?, ?>> ops = new ArrayList<>();

    public StreamBinding(ObservableList<E> source) {
        this.source = source;
    }

    public <R> StreamBinding<R> map(Function<E, R> f) {
        ops.add(new MapOp(f));
        return (StreamBinding<R>) this;
    }

    public ObservableList<E> toList() {
        ObservableList<E> list = new ObservableListImpl<>();
        ops.add(new Finish<>(list));
        source.enumerateAndSubscribe(this::sourceChange);
        return list;
    }

    private <F> void sourceChange(ListChange<?> c) {
        Op<F, ?> first = (Op<F, ?>) ops.get(0);
        if (c instanceof ListChange.ListAdd a)
            for (int i = 0; i < a.elements().size(); i++)
                first.add(a.index() + i, (F) a.elements().get(i), ops.listIterator(1));
        else {
            ListChange.ListRemove<F> r = (ListChange.ListRemove<F>) c;
            for (int i = 0; i < r.elements().size(); i++)
                first.add(r.index() + i, (F) r.elements().get(i), ops.listIterator(1));
        }
    }

    private interface Op<S, T> {
        void add(int index, S e, Iterator<Op<?, ?>> ops);

        void remove(int index, S e, Iterator<Op<?, ?>> ops);
    }

    private static record MapOp<S, T>(Function<S, T> f) implements Op<S, T> {

        @Override
        public void add(int index, S e, Iterator<Op<?, ?>> ops) {
            ((Op<T, ?>) ops.next()).add(index, f.apply(e), ops);
        }

        @Override
        public void remove(int index, S e, Iterator<Op<?, ?>> ops) {
            ((Op<T, ?>) ops.next()).remove(index, f.apply(e), ops);
        }
    }

    private static record Finish<E>(ObservableList<E> list) implements Op<E, Void> {

        @Override
        public void add(int index, E e, Iterator<Op<?, ?>> ops) {
            list.add(index, e);
        }

        @Override
        public void remove(int index, E e, Iterator<Op<?, ?>> ops) {
            Object o = list.remove(index);
            if (o != e)
                throw new RuntimeException("removed object differs: expected " + e +
                        " at index " + index + ", but got " + o);
        }
    }

}
