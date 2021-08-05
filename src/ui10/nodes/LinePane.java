package ui10.nodes;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.geom.Num;
import ui10.geom.Size;
import ui10.image.Color;
import ui10.image.RGBColor;
import ui10.layout.BoxConstraints;

public class LinePane extends Pane {

    public final ScalarProperty<Num> width = ScalarProperty.create();
    public final ScalarProperty<Color> color = ScalarProperty.createWithDefault(RGBColor.BLACK);

    public LinePane() {
    }

    public LinePane(Num width, Color color) {
        this.width.set(width);
        this.color.set(color);
    }

    public LinePane(ObservableScalar<Num> width, ObservableScalar<Color> color) {
        this.width.bindTo(width);
        this.color.bindTo(color);
    }

    @Override
    protected ObservableScalar<Node> paneContent() {
        return ObservableScalar.ofConstant(new PrimitiveNode(this) {

            @SuppressWarnings("SuspiciousNameCombination")
            @Override
            protected ObservableScalar<Size> size(ObservableScalar<BoxConstraints> constraintsObservable) {
                return ObservableScalar.binding(constraintsObservable, width,
                        (constraints, width) -> constraints.clamp(new Size(width, width)));
            }
        });
    }
}
