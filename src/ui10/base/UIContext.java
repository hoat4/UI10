package ui10.ui6;

import ui10.nodes.EventLoop;

import java.util.function.Consumer;

public interface UIContext {

    EventLoop eventLoop();

    void requestLayout(LayoutTask task);

    record LayoutTask(RenderableElement element, Runnable task) {}
}
