package ui10.binding9;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class OList<E> extends Observable implements List<E> {

    private final List<E> delegate;

    public OList() {
        this(new ArrayList<>());
    }

    public OList(List<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int size() {
        onRead();
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        onRead();
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        onRead();
        return delegate.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        onRead();
        return delegate.iterator(); // TODO Iterator::remove
    }

    @Override
    public Object[] toArray() {
        onRead();
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        onRead();
        return delegate.toArray(a);
    }

    @Override
    public boolean add(E e) {
        if (delegate.add(e)) {
            onWrite();
            return true;
        } else
            return false;
    }

    @Override
    public boolean remove(Object o) {
        if (delegate.remove(o)) {
            onWrite();
            return true;
        } else
            return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        onRead();
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (delegate.addAll(c)) {
            onWrite();
            return true;
        } else
            return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (delegate.addAll(index, c)) {
            onWrite();
            return true;
        } else
            return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (delegate.removeAll(c)) {
            onWrite();
            return true;
        } else
            return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (delegate.retainAll(c)) {
            onWrite();
            return true;
        } else
            return false;
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        delegate.replaceAll(operator);
        onWrite();
    }

    @Override
    public void sort(Comparator<? super E> c) {
        delegate.sort(c);
        onWrite();
    }

    @Override
    public void clear() {
        if (!isEmpty()) {
            delegate.clear();
            onWrite();
        }
    }

    @Override
    public boolean equals(Object o) {
        onRead();
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        onRead();
        return delegate.hashCode();
    }

    @Override
    public E get(int index) {
        onRead();
        return delegate.get(index);
    }

    @Override
    public E set(int index, E element) {
        onWrite();
        return delegate.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        onWrite();
        delegate.add(index, element);
    }

    @Override
    public E remove(int index) {
        onWrite();
        return delegate.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        onRead();
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        onRead();
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Spliterator<E> spliterator() {
        onRead();
        return delegate.spliterator();
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        onRead();
        return delegate.toArray(generator);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        if (delegate.removeIf(filter)) {
            onWrite();
            return true;
        }
        return false;
    }

    @Override
    public Stream<E> stream() {
        onRead();
        return delegate.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        onRead();
        return delegate.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        onRead();
        delegate.forEach(action);
    }
}
