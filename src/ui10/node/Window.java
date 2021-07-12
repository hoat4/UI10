package ui10.node;

import ui10.binding.ScalarProperty;

public class Window extends ReplaceableNode {

    private Node content;

    public ScalarProperty<Node> content() {
        return property((Window w) -> w.content, (w, n) -> w.content = n);
    }

    @Override
    protected Node makeReplacement() {
        throw new UnsupportedOperationException();
    }
}
