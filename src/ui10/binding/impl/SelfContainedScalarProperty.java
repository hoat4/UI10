package ui10.binding.impl;

import ui10.binding.*;
import ui10.base.UIThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("rawtypes")
public class SelfContainedScalarProperty<T> implements ScalarProperty<T>, Consumer {

    // Consumer raw type, hogy a bridge method ne szemetelje stack trace-t

    // TODO mi legyen ChangeEvent.container értéke?

    private T value;

    private final List<Consumer<? super ChangeEvent<T>>> subscribers = new ArrayList<>();

    private ObservableScalar<? extends T> boundTo;

    private ObservableList<PropertyTransformation<T>> transformations;
    private Scope transformScope;
    private T transformedValue;

    private final String name;

    public SelfContainedScalarProperty(String name) {
        this.name = name;
    }

    @Override
    public void subscribe(Consumer<? super ChangeEvent<T>> subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(Consumer<? super ChangeEvent<T>> subscriber) {
        if (!subscribers.remove(subscriber))
            throw new IllegalArgumentException("not subscribed: " + subscriber);
    }

    @Override
    public T get() {
        if (Thread.currentThread() instanceof UIThread uiThread) {
            ReadTransaction t = uiThread.currentReadTransaction;
            if (t != null)
                t.onRead(this);
        } else {
            System.err.println("Property read from non-UI thread: " + this + ", " + Thread.currentThread().getName());
        }
        return transformedValue;
    }

    @Override
    public ScalarProperty<T> set(T value) {
        // TODO unbind?
        if (!Objects.equals(this.value, value)) {
            // private void setImpl(T value) {
            T oldValue = transformedValue;
            this.value = value;

            if (transformations != null) {
                if (transformScope != null)
                    transformScope.close();
                transformScope = new Scope();

                for (PropertyTransformation<T> t : transformations)
                    value = t.apply(value, transformScope);
                if (Objects.equals(oldValue, transformedValue = value)) // itt assignoljunk?
                    return this;
            } else
                transformedValue = value;

            int n = subscribers.size();
            for (int i = 0; i < n; i++)
                subscribers.get(i).accept(new ChangeEvent<>(null, oldValue, transformedValue));
            // }
        }

        return this;
    }

    @Override
    public void accept(Object event) {
        T value = ((ChangeEvent<? extends T>) event).newValue();

        // set(T) inline-olva, hogy ne szemetelje stack trace-t

        if (!Objects.equals(this.value, value)) {
            // private void setImpl(T value) {
            T oldValue = transformedValue;
            this.value = value;

            if (transformations != null) {
                if (transformScope != null)
                    transformScope.close();
                transformScope = new Scope();

                for (PropertyTransformation<T> t : transformations)
                    value = t.apply(value, transformScope);
                if (Objects.equals(oldValue, transformedValue = value)) // itt assignoljunk?
                    return;
            } else
                transformedValue = value;

            int n = subscribers.size();
            for (int i = 0; i < n; i++)
                subscribers.get(i).accept(new ChangeEvent<>(null, oldValue, transformedValue));
            // }
        }
    }

    @Override
    public void bindTo(ObservableScalar<? extends T> other, Scope scope) {
        Objects.requireNonNull(other);
        if (boundTo != null)
            boundTo.unsubscribe(this);
        boundTo = other;
        boundTo.subscribe(this);
        set(boundTo.get());

        if (scope != null)
            scope.onClose(() -> {
                if (boundTo != other)
                    throw new IllegalStateException();
                else {
                    boundTo = null;
                    other.unsubscribe(this);
                }
            });
    }

    @Override
    public ObservableList<PropertyTransformation<T>> transformations() {
        if (transformations == null) {
            transformations = new ObservableListImpl<>();
            transformations.subscribe(c -> {
                T value = this.value;

                //   private void setImpl(T value) {
                T oldValue = transformedValue;
                //       this.value = value;

                //       if (transformations != null) {
                if (transformScope != null)
                    transformScope.close();
                transformScope = new Scope();

                for (PropertyTransformation<T> t : transformations)
                    value = t.apply(value, transformScope);
                if (Objects.equals(oldValue, transformedValue = value)) // itt assignoljunk?
                    return;
                //       } else
                //           transformedValue = value;

                int n = subscribers.size();
                for (int i = 0; i < n; i++)
                    subscribers.get(i).accept(new ChangeEvent<>(null, oldValue, transformedValue));
                //     }
            });
        }
        return transformations;
    }

    @Override
    public String toString() {
        return name + "=" + get();
    }
}
