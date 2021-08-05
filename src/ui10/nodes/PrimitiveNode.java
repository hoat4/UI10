package ui10.nodes;

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


    @Override
    protected ObservableScalar<Size> makeLayoutThread(ObservableScalar<BoxConstraints> in, boolean apply, Scope scope) {
        return size(in);
    }

    @Override
    public ObservableList<? extends Node> children() {
        return ObservableList.constantEmpty();
    }

    protected abstract ObservableScalar<Size> size(ObservableScalar<BoxConstraints> constraintsObservable);
}
