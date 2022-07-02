package ui10.shell.renderer.java2d;

import ui10.base.*;
import ui10.binding7.PropertyBasedModel;
import ui10.geom.Size;
import ui10.input.pointer.MouseEvent;
import ui10.layout.BoxConstraints;

import java.awt.Graphics2D;
import java.util.List;

public class J2DMouseTarget extends AbstractJ2DContainer<MouseTarget>
        implements PropertyBasedModel.PropertyBasedModelListener {

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

    @Override
    public void modelInvalidated() {
        // TODO cursor frissítése
    }
}
