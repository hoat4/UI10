package ui10.binding;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ObservableListImpl<E> extends AbstractList<E> implements ObservableList<E> {

    private final List<E> list;
    private final List<Consumer<? super ListChange<E>>> subscribers = new ArrayList<>();

    public ObservableListImpl() {
        list = new ArrayList<>();
    }

    public ObservableListImpl(Consumer<ListChange<E>> initialSubscriber) {
        list = new ArrayList<>();
        subscribers.add(initialSubscriber);
    }

    public ObservableListImpl(List<E> list) {
        this.list = list;
    }

    public static <E> ObservableList<E> createMutable(List<E> children) {
        if (children instanceof ArrayList<E> a)
            return new ObservableListImpl<>(a);
        else
            return new ObservableListImpl<>(new ArrayList<>(children));
    }


    public static <E> ObservableList<E> createMutable(E... children) {
        return new ObservableListImpl<>(new ArrayList<>(Arrays.asList(children)));
    }

    @Override
    public void subscribe(Consumer<? super ListChange<E>> consumer) {
        subscribers.add(consumer);
    }

    @Override
    public void unsubscribe(Consumer<? super ListChange<E>> listChangeConsumer) {
        if (!subscribers.remove(listChangeConsumer))
            throw new IllegalArgumentException("not subscribed: " + listChangeConsumer);
    }

    @Override
    public E get(int i) {
        return list.get(i);
    }

    @Override
    public int size() {
        return list.size();
    }

    // TODO ez a List.of itt nem jó, mert nem fogad el nullt

    @Override
    public void add(int index, E element) {
        list.add(index, element);
        onChange(new ListChange<>(index, List.of(), List.of(element)));
    }

    @Override
    public E remove(int index) {
        E value = list.remove(index);
        onChange(new ListChange<>(index, List.of(value), List.of()));
        return value;
    }

    private void onChange(ListChange<E> change) {
        for (int i = 0; i < subscribers.size(); i++)
            subscribers.get(i).accept(change);
    }

    @Override
    public E set(int index, E element) {
        E oldValue = list.set(index, element);
        onChange(new ListChange<>(index, List.of(oldValue), List.of(element)));
        return oldValue;
    }

    @Override
    public void setAll(List<E> elements) {
        list.clear();
        List<E> prev = List.copyOf(list);
        list.addAll(elements);
        onChange(new ListChange<>(0, prev, List.copyOf(list)));
    }
}
