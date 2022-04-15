package ui10.binding;

public interface EventBus<E> extends Observable<E> {

    void postEvent(E event);
}
