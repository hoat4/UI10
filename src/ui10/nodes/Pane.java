package ui10.nodes;

import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;
import ui10.node.Node;

import java.util.List;

public abstract class Pane extends Node {

    private final ObservableList<Node> children = new ObservableListImpl<>();

    public Pane() {
    }

    public Pane(Node... children) {
        this.children.addAll(List.of(children));
    }

    @Override
    protected ObservableList<Node> createChildren() {
        return children;
    }
}
