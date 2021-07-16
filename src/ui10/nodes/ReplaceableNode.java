package ui10.nodes;

import ui10.binding.ObservableList;
import ui10.layout.BoxConstraints;
import ui10.node.Node;

import java.util.Objects;

import static ui10.geom.Point.ORIGO;

public abstract class ReplaceableNode extends Node {

    private Node replacement;

    protected abstract Node makeReplacement();

    private Node replacement() {
        if (replacement == null) {
            replacement = makeReplacement();
            Objects.requireNonNull(replacement, () -> "makeReplacement() returned null: " + this);
        }
        return replacement;
    }

    @Override
    protected ObservableList<Node> createChildren() {
        return ObservableList.ofConstantElement(replacement());
    }

    @Override
    protected Node.Layout computeLayout(BoxConstraints constraints) {
        Layout contentLayout = replacement().layout(constraints);
        return new Layout(constraints, contentLayout.size) {
            @Override
            protected void apply() {
                contentLayout.apply(ORIGO);
            }
        };
    }
}
