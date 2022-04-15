package ui10.decoration;

import ui10.geom.Size;
import ui10.base.RenderableElement;
import ui10.decoration.css.Length;

public class DecorationContext {

    public RenderableElement lowestRenderableElement;

    public Size parentSize;

    public int length(Length length) {
        if (length.relative() != 0)
            throw new IllegalArgumentException();
        return length.px() >> 14;
    }

    public int length(Length length, int parent) {
        return (length.px() + length.relative() * parent) >> 14;
    }
}
