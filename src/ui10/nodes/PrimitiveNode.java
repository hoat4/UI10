package ui10.nodes;

import ui10.binding.Observable;
import ui10.binding.ObservableList;
import ui10.binding.ObservableScalar;
import ui10.binding.Scope;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

public abstract class PrimitiveNode extends Node {

    public final Pane target;

    public PrimitiveNode(Pane target) {
        this.target = target;
    }

    protected void sizeDependsOn(Observable<?> o) {
        o.subscribe(e->{
            LayoutNodeImpl n = parentLayoutNode();
            if (n != null)
                n.requestLayout(this);
        });
    }

    private LayoutNodeImpl parentLayoutNode() {
        Node n = parent.get();
        while (n != null && !(n instanceof LayoutNodeImpl))
            n = n.parent.get();
        return (LayoutNodeImpl) n;
    }

    @Override
    public ObservableList<? extends Node> children() {
        return ObservableList.constantEmpty();
    }
}
