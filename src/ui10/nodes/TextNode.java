package ui10.nodes;

import ui10.binding.ObservableList;
import ui10.binding.ScalarProperty;
import ui10.font.FontMetrics;
import ui10.layout.BoxConstraints;
import ui10.node.Node;

public class TextNode extends Node {

    private String text;

    public TextNode() {
    }

    public TextNode(String text) {
        this.text = text;
    }

    public ScalarProperty<String> text() {
        return property((TextNode n) -> n.text, (n, v) -> n.text = v);
    }

    @Override
    protected ObservableList<Node> createChildren() {
        return null;
    }

    @Override
    protected Layout computeLayout(BoxConstraints constraints) {
        FontMetrics m = null;//font().get().renderer().measure(text, font().get());
        return new Layout(constraints, m.size()) {
            @Override
            protected void apply() {
            }
        };
    }
}
