package ui10.binding9;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class Observer2 {

    final List<Observable> observables;

    static final ThreadLocal<Observer2> currentObserverHolder = new ThreadLocal<>();

    public Observer2() {
        observables = new ArrayList<>();
    }

    protected void clear() {
        for (Observable observable : observables)
            observable.observers.remove(this);
        observables.clear();
    }

    protected abstract void invalidate();

    public <R> R executeObserved(Supplier<R> task) {
        Observer2 previous = Observer2.currentObserverHolder.get();
        currentObserverHolder.set(this);
        try {
            return task.get();
        } finally {
            if (previous == null)
                currentObserverHolder.remove();
            else
                currentObserverHolder.set(previous);
        }
    }
}
