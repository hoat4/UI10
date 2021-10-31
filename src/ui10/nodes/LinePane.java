package ui10.nodes;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;

import ui10.geom.Size;
import ui10.image.Colors;
import ui10.image.Fill;
import ui10.layout.BoxConstraints;

public class LinePane extends Pane {

    public final ScalarProperty<Integer> width = ScalarProperty.create();
    public final ScalarProperty<Fill> fill = ScalarProperty.createWithDefault(Colors.BLACK);

    public LinePane() {
    }

    public LinePane(int width, Fill fill) {
        this.width.set(width);
        this.fill.set(fill);
    }

    public LinePane(ObservableScalar<Integer> width, ObservableScalar<? extends Fill> fill) {
        this.width.bindTo(width);
        this.fill.bindTo(fill);
    }

    @Override
    protected ObservableScalar<Node> paneContent() {
        return ObservableScalar.ofConstant(new PrimitiveNode(this) {

            {
                sizeDependsOn(width);
            }

            @Override
            public Size determineSize(BoxConstraints constraints) {
                return constraints.clamp(new Size(width.get(), width.get()));
            }
        });
    }
}
