package ui10.renderer.java2d;

import ui10.binding.ObservableList;
import ui10.layout.BoxConstraints;
import ui10.node.Node;

import java.awt.*;

abstract class J2DNode extends Node {

    @Override
    public ObservableList<Node> createChildren() {
        return null;
    }

    @Override
    protected Node.Layout computeLayout(BoxConstraints constraints) {
        throw new UnsupportedOperationException();
    }

    abstract void draw(Graphics2D g);
}
