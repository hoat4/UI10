package ui10.binding9;

import java.util.Objects;
import java.util.function.Supplier;

public class Bindings {

    public static <R2> R2 onInvalidated(Supplier<R2> task, Observer observer) {
        return executeObserved(task, new Observer2() {
            @Override
            protected void invalidate() {
                observer.invalidate();
            }
        });
    }

    public static void onInvalidated(Runnable task, Observer observer) {
        executeObserved(() -> {
            task.run();
            return null;
        }, new Observer2() {
            @Override
            protected void invalidate() {
                observer.invalidate();
            }
        });
    }

    public static void repeatIfInvalidated(Runnable r) {
        onInvalidated(r, () -> repeatIfInvalidated(r));
    }

    public static <T> T onChange(Supplier<T> task, Observer observer) {
        OVal<T> val = new OVal<>();
        repeatIfInvalidated(() -> val.set(task.get()));
        return onInvalidated(val::get, observer);
    }

    public static <T> T onFirstChange(Supplier<T> task, Observer observer) {

        class ObserverImpl extends Observer2 {

            T prevValue;
            int state;

            @Override
            protected void invalidate() {
                assert state == 1 : state+", "+observer;
                T newValue = executeObserved(task, this);
                if (!Objects.equals(prevValue, newValue)) {
                    state = 2;
                    clear();
                    observer.invalidate();
                }
            }
        }

        ObserverImpl o = new ObserverImpl();
        o.prevValue = executeObserved(task, o);
        o.state = 1;
        return o.prevValue;
    }

    public static <R> R executeObserved(Supplier<R> task, Observer2 observer) {
        Observer2 previous = Observer2.currentObserverHolder.get();
        Observer2.currentObserverHolder.set(observer);
        try {
            return task.get();
        } finally {
            if (previous == null)
                Observer2.currentObserverHolder.remove();
            else
                Observer2.currentObserverHolder.set(previous);
        }
    }

    public static void withoutObserver(Runnable runnable) {
        Observer2 previous = Observer2.currentObserverHolder.get();
        Observer2.currentObserverHolder.set(null);
        try {
            runnable.run();
        } finally {
            if (previous == null)
                Observer2.currentObserverHolder.remove();
            else
                Observer2.currentObserverHolder.set(previous);
        }
    }
}
