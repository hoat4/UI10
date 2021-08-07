package ui10.nodes;

import ui10.binding.ObservableList;

public abstract class LayoutNode extends Node {

    protected final ObservableList<? extends Node> children;

    public LayoutNode(ObservableList<? extends Node> children) {
        this.children = children;
        children.enumerateAndSubscribe(c -> {

        });
    }

    @Override
    public ObservableList<? extends Node> children() {
        return children;
    }
}
