package ui10.node;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.nodes.ReplaceableNode;

import java.util.Objects;

public class Window extends ReplaceableNode {

    private Node content;

    public ScalarProperty<Node> content() {
        return property((Window w) -> w.content, (w, n) -> w.content = n);
    }

    public ObservableScalar<Boolean> shown() {
        return parent().map(Objects::nonNull);
    }

    @Override
    protected Node makeReplacement() {
        throw new UnsupportedOperationException();
    }
}
