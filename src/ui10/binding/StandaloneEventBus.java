package ui10.binding;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class StandaloneEventBus<E> implements EventBus<E> {

    private final List<Consumer<? super E>> subscribers = new ArrayList<>();

    @Override
    public void postEvent(E event) {
        subscribers.forEach(s -> s.accept(event));
    }

    @Override
    public void subscribe(Consumer<? super E> subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(Consumer<? super E> subscriber) {
        if (!subscribers.remove(subscriber))
            throw new RuntimeException("not subscribed: " + subscriber);
    }
}
