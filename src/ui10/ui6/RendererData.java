package ui10.ui6;

import ui10.nodes.EventLoop;

public interface RendererData {

    void invalidateRendererData(); //  // name clash with java.awt.Component::invalidate

    EventLoop eventLoop();

    void invalidateLayout();
}
