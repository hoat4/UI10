package ui10.nodes;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.geom.Size;
import ui10.image.Fill;
import ui10.layout.BoxConstraints;

public class FilledRectanglePane extends Pane {

    public final ScalarProperty<Fill> fill = ScalarProperty.create();
    public final ScalarProperty<Integer> radius = ScalarProperty.createWithDefault(0);

    public FilledRectanglePane() {
    }

    public FilledRectanglePane(Fill fill) {
        this.fill.set(fill);
    }

    public FilledRectanglePane(ObservableScalar<? extends Fill> fill) {
        this.fill.bindTo(fill);
    }

    @Override
    protected ObservableScalar<Node> paneContent() {
        return ObservableScalar.ofConstant(new PrimitiveNode(this) {

            @Override
            public Size determineSize(BoxConstraints constraints) {
                return constraints.min();
            }
        });
    }

    @Override
    public String toString() {
        return "FilledPane (fill=" + fill.get() + ")";
    }
}
