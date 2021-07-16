package ui10.binding;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ObservableListImpl<E> extends AbstractList<E> implements ObservableList<E> {

    private final List<E> list;
    private final List<Consumer<ListChange<E>>> subscribers = new ArrayList<>();

    public ObservableListImpl() {
        list = new ArrayList<>();
    }

    public ObservableListImpl(List<E> list) {
        this.list = list;
    }

    @Override
    public void subscribe(Consumer<ListChange<E>> consumer) {
        subscribers.add(consumer);
    }

    @Override
    public void unsubscribe(Consumer<ListChange<E>> listChangeConsumer) {
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

    @Override
    public void add(int index, E element) {
        list.add(index, element);
        onChange(new ListChange.ListAdd<>(index, List.of(element)));
    }

    @Override
    public E remove(int index) {
        E value = list.remove(index);
        onChange(new ListChange.ListRemove<>(index, List.of(value)));
        return value;
    }

    private void onChange(ListChange<E> change) {
        for (int i = 0; i < subscribers.size(); i++)
            subscribers.get(i).accept(change);
    }

    @Override
    public E set(int index, E element) {
        E prev = remove(index);
        add(index, element);
        return prev;
    }


}
