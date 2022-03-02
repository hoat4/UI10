package ui10.base;

public interface RendererData {

    void invalidateRendererData(); // name clash with java.awt.Component::invalidate
}
