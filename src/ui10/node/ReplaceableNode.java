package ui10.node;

import ui10.binding.ObservableList;
import ui10.layout.BoxConstraints;

import java.util.Objects;

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
    public ObservableList<Node> children() {
        return ObservableList.ofConstantElement(replacement());
    }

    @Override
    public Layout layout(BoxConstraints constraints) {
        Layout contentLayout = replacement().layout(constraints);
        return new Layout(contentLayout.size, contentLayout::apply);
    }
}
