package ui10.binding;

import java.util.function.Consumer;

public interface Observable<E> {

    void subscribe(Consumer<E> subscriber);

    void unsubscribe(Consumer<E> subscriber);
}
