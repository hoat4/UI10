package ui10.nodes;

import ui10.binding.ObservableListImpl;
import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.binding.Scope;
import ui10.geom.Num;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.image.Color;
import ui10.image.RGBColor;
import ui10.layout.BoxConstraints;

import static ui10.binding.ObservableScalar.binding;
import static ui10.geom.Num.*;
import static ui10.geom.Point.ORIGO;

public class StrokedRectanglePane extends Pane {

    public final ScalarProperty<Num> width = ScalarProperty.create();
    public final ScalarProperty<Color> color = ScalarProperty.create();

    public StrokedRectanglePane() {
        this(ONE, RGBColor.BLACK);
    }

    public StrokedRectanglePane(Num width, Color color) {
        this.width.set(width);
        this.color.set(color);
    }

    public StrokedRectanglePane(ObservableScalar<Num> width, ObservableScalar<Color> color) {
        this.width.bindTo(width);
        this.color.bindTo(color);
    }

    @Override
    protected ObservableScalar<Node> paneContent() {
        LinePane top = new LinePane(width, color), right = new LinePane(width, color);
        LinePane bottom = new LinePane(width, color), left = new LinePane(width, color);

        return ObservableScalar.ofConstant(new LayoutNode(ObservableListImpl.createMutable(top, right, bottom, left)) {
            @Override
            protected ObservableScalar<Size> makeLayoutThread(ObservableScalar<BoxConstraints> in, boolean apply, Scope scope) {
                // TODO itt inkább fixed constraints kéne

                ObservableScalar<BoxConstraints> cHoriz = binding(in, width, (c, w)->c.withHeight(w, w));
                ObservableScalar<BoxConstraints> cVert = binding(in, width, (c, w)->c.withWidth(w, w));

                top.layoutThread(cHoriz, apply, scope);
                right.layoutThread(cVert, apply, scope);
                bottom.layoutThread(cHoriz, apply, scope);
                left.layoutThread(cVert, apply, scope);

                return binding(in, width, (constraints, w)->{
                    Size size = constraints.clamp(new Size(w.mul(TWO), w.mul(TWO)));
                    if (apply) {
                        top.position.set(ORIGO);
                        right.position.set(new Point(size.width().sub(w), ZERO));
                        bottom.position.set(new Point(ZERO, size.height().sub(w)));
                        left.position.set(ORIGO);
                    }
                    return size;
                });
            }
        });
    }
}
