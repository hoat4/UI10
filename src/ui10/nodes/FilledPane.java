package ui10.nodes;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.geom.Size;
import ui10.image.Color;
import ui10.layout.BoxConstraints;

public class FilledPane extends Pane {

    public final ScalarProperty<Color> color = ScalarProperty.create();

    public FilledPane() {
    }

    public FilledPane(Color color) {
        this.color.set(color);
    }

    public FilledPane(ObservableScalar<Color> color) {
        this.color.bindTo(color);
    }

    @Override
    protected ObservableScalar<Node> paneContent() {
        return ObservableScalar.ofConstant(new PrimitiveNode(this) {

            @Override
            protected ObservableScalar<Size> size(ObservableScalar<BoxConstraints> constraintsObservable) {
                return constraintsObservable.map(BoxConstraints::min);
            }
        });
    }

    @Override
    public String toString() {
        return "FilledPane (color="+color.get()+")";
    }
}
