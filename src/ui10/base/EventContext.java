package ui10.base;

public class EventContext {
    public boolean stopPropagation;

    public void stopPropagation() {
        stopPropagation = true;
    }
}
