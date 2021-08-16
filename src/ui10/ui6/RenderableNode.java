package ui10.ui6;

public abstract class RenderableNode implements Node {

    public RendererData rendererData;

    protected void invalidate() {
        if (rendererData != null)
        rendererData.invalidate();
    }
}
