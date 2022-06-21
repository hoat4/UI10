package ui10.base;

public interface UIContext {

    EventLoop eventLoop();

    void requestLayout(LayoutTask task);

    record LayoutTask(Element element, Runnable task) {}
}
