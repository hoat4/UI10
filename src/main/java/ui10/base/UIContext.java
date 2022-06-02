package ui10.base;

public interface UIContext {

    EventLoop eventLoop();

    void requestLayout(LayoutTask task);

    ViewProvider viewProvider();

    record LayoutTask(RenderableElement element, Runnable task) {}
}
