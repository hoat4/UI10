package ui10.shell.renderer.java2d;

import ui10.base.*;
import ui10.geom.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class J2DContainer extends AbstractJ2DContainer<Container> {

    public J2DContainer(J2DRenderer renderer, Container node) {
        super(renderer, node);
    }

    @Override
    protected Element getContent() {
        return node.contentProp.get();
    }
}
