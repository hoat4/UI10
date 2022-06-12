package ui10.decoration;

import ui10.base.Element;
import ui10.decoration.css.Length;
import ui10.geom.Size;

public class DecorationContext {

    public final Element element;
    public Size parentSize;
    private final int emSize;

    public DecorationContext(Element element, int emSize) {
        this.element = element;
        this.emSize = emSize;
    }

    public int length(Length length) {
        if (length.relative() != 0)
            throw new IllegalArgumentException();
        if (length.em() != 0 && emSize == -1)
            throw new IllegalArgumentException("can't compute absolute value of "+length+" because no em size present for "+element);
        return (length.px() + emSize * length.em()) >> 14;
    }

    public int length(Length length, int parent) {
        if (length.em() != 0 && emSize == -1)
            throw new IllegalArgumentException();
        return (length.px() + emSize * length.em() + length.relative() * parent) >> 14;
    }
}
