package ui10.shell.renderer.java2d;

import ui10.base.*;
import ui10.input.pointer.MouseEvent;

import java.util.List;

public class J2DMouseTarget extends AbstractJ2DContainer<MouseTarget> {

    public J2DMouseTarget(J2DRenderer renderer, MouseTarget node) {
        super(renderer, node);
    }

    @Override
    protected Element getContent() {
        return node.content;
    }

    @Override
    public boolean captureMouseEvent(MouseEvent p, List<MouseTarget> l, EventContext eventContext) {
        if (shape.contains(J2DUtil.point(p.point())))
            l.add(node);
        return false;
    }
}
