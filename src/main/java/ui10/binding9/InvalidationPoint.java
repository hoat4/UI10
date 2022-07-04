package ui10.binding9;

public class InvalidationPoint extends Observable {

    public void subscribe() {
        onRead();
    }

    public void invalidate() {
        onWrite();
    }
}
