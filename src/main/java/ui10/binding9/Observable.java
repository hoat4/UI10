package ui10.binding9;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class Observable {

    Set<ObserverData> observers = new LinkedHashSet<>();

    protected void onRead() {
        ObserverData observer = ObserverData.currentObserverHolder.get();
        if (observer != null) {
            observer.observables.add(this);
            observers.add(observer);
        }
    }

    protected void onWrite() {
        ObserverData currentObserver = ObserverData.currentObserverHolder.get();

        Set<ObserverData> s = observers;
        observers = new LinkedHashSet<>();

        for (ObserverData o : s)
             o.clear();

        for (ObserverData observer : s)
            if (observer != currentObserver)
                observer.invalidate();
    }
}
