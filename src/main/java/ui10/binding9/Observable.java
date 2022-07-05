package ui10.binding9;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class Observable {

    Set<Observer2> observers = new LinkedHashSet<>();

    protected void onRead() {
        Observer2 observer = Observer2.currentObserverHolder.get();
        if (observer != null) {
            observer.observables.add(this);
            observers.add(observer);
        }
    }

    protected void onWrite() {
        Observer2 currentObserver = Observer2.currentObserverHolder.get();

        Bindings.withoutObserver(() -> {
            Set<Observer2> s = observers;
            observers = new LinkedHashSet<>();

            for (Observer2 o : s)
                if (o != currentObserver)
                    o.clear();

            for (Observer2 observer : s)
                if (observer != currentObserver)
                    observer.invalidate();
        });
    }
}
