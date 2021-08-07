package ui10.nodes;

import ui10.binding.Observable;
import ui10.binding.ObservableList;
import ui10.binding.ObservableScalar;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

import java.util.Collection;
import java.util.Objects;

public abstract class Layout {

    final ObservableList<? extends Node> children;
    final LayoutNodeImpl node; // ezt később inicializáljuk, hogy lássa childrent

    public Layout(ObservableList<? extends Node> children) {
        this.children = Objects.requireNonNull(children);
        this.node = new LayoutNodeImpl(this);
    }

    public Layout(ObservableScalar<? extends Node> child) {
        this.children = ObservableList.of(child);
        this.node = new LayoutNodeImpl(this);
    }

    protected void dependsOn(Observable<?> o) {
        o.subscribe(e -> node.requestLayout(o));
    }

    protected abstract Size determineSize(BoxConstraints constraints);

    protected abstract void layout(Collection<?> updated);

    public Node asNode() {
        return node;
    }

    public ObservableScalar<? extends Node>  asNodeObservable() {
        return ObservableScalar.ofConstant(node);
    }
}
