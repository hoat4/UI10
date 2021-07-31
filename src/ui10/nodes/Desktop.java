package ui10.nodes;

import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;
import ui10.layout.BoxConstraints;
import ui10.node.Node;

public class Desktop extends Node {

    public ObservableList<Window> windows() {
        return (ObservableList<Window>) (ObservableList<?>) children();
    }

    @Override
    protected ObservableList<Node> createChildren() {
        return new ObservableListImpl<>();
    }

    @Override
    protected Node.Layout computeLayout(BoxConstraints constraints) {
        throw new UnsupportedOperationException();
    }
}
