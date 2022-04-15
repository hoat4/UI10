package ui10.binding;

import ui10.geom.Size;

import java.util.List;
import java.util.function.Consumer;

public interface Observable<E> {

    void subscribe(Consumer<? super E> subscriber);

    void unsubscribe(Consumer<? super E> subscriber);
}
