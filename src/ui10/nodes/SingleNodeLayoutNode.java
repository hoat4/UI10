package ui10.nodes;

import ui10.binding.ObservableList;
import ui10.binding.ScalarProperty;
import ui10.node.Node;

public abstract class SingleNodeLayoutNode extends Node {

    // TODO a Layout-ot invalidálni kéne, ha a content megváltozik

    private Node content;

    public SingleNodeLayoutNode() {
    }

    public SingleNodeLayoutNode(Node content) {
        this.content = content;
    }

    public ScalarProperty<Node> content() {
        return property((SingleNodeLayoutNode n) -> n.content, (n, v) -> n.content = v);
    }

    @Override
    protected ObservableList<Node> createChildren() {
        return ObservableList.of(content());
    }
}
