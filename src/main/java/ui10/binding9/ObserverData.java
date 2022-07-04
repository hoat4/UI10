package ui10.binding9;

import java.util.ArrayList;
import java.util.List;

abstract class ObserverData {

    final List<Observable> observables;

    static final ThreadLocal<ObserverData> currentObserverHolder = new ThreadLocal<>();

    public ObserverData() {
        observables = new ArrayList<>();
    }

    void clear() {
        for (Observable observable : observables)
            observable.observers.remove(this);
        observables.clear();
    }

    abstract void invalidate();
}
