package ui10.binding;

import java.util.function.Consumer;

public interface Observable<E> {

    void subscribe(Consumer<? super E> subscriber);

    void unsubscribe(Consumer<? super E> subscriber);
}
