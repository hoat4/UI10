package ui10.shell.renderer.java2d;

import ui10.base.*;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.input.pointer.MouseEvent;
import ui10.layout.BoxConstraints;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class J2DContainer extends AbstractJ2DContainer<Container> implements Container.ContainerListener {

    private final List<J2DRenderableElement<?>> children = new ArrayList<>();
    private Shape shape2;

    public J2DContainer(J2DRenderer renderer, Container node) {
        super(renderer, node);
    }

    @Override
    public void contentChanged() {
        invalidate();
    }

    @Override
    protected Element getContent() {
        return node.getContent();
    }
}
