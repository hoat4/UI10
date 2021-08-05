package ui10.nodes;

import ui10.binding.*;
import ui10.decoration.Tag;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.input.InputEnvironment;
import ui10.layout.BoxConstraints;

public abstract class Node {

    public final ScalarProperty<Point> position = ScalarProperty.create();
    public final ScalarProperty<Size> size = ScalarProperty.create();

    public final ScalarProperty<InputEnvironment> inputEnvironment = ScalarProperty.create();

    final ObservableList<Tag> tags = new ObservableListImpl<>();
    public Object rendererData;

    public final ScalarProperty<Boolean> layoutActive = ScalarProperty.createWithDefault(false);

    public final ObservableList<Tag> tags() {
        return tags;
    }

    public ObservableScalar<Size> layoutThread(ObservableScalar<BoxConstraints> in, boolean apply, Scope scope) {
        if (apply && layoutActive.get())
            throw new IllegalStateException(this + " already has an active layout");

        ObservableScalar<Size> out = makeLayoutThread(in, apply, scope);

        out.getAndSubscribe(scope, size -> {
            if (!in.get().contains(size))
                throw new IllegalStateException();
            if (apply)
                this.size.set(size);
        });

        if (apply)
            layoutActive.set(true);

        return out;
    }

    protected abstract ObservableScalar<Size> makeLayoutThread(ObservableScalar<BoxConstraints> in,
                                                               boolean apply, Scope scope);

    public abstract ObservableList<? extends Node> children();
}
