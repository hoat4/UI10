package ui10.nodes;

import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;
import ui10.binding.ScalarProperty;
import ui10.decoration.Tag;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

public abstract class Node {

    public final ScalarProperty<Node> parent = ScalarProperty.create("Node.parent");
    public final ScalarProperty<Rectangle> bounds = ScalarProperty.create("Node.bounds");
    public final ScalarProperty<Context> context = ScalarProperty.create("Node.context");

    final ObservableList<Tag> tags = new ObservableListImpl<>();
    public Object rendererData;

    public final ObservableList<Tag> tags() {
        return tags;
    }

    public abstract Size determineSize(BoxConstraints constraints);

    public abstract ObservableList<? extends Node> children();
}
