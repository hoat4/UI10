package ui10.nodes;

import ui10.binding.ObservableListImpl;
import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.geom.Num;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.image.Color;
import ui10.image.RGBColor;
import ui10.layout.BoxConstraints;

import java.util.Collection;

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
    protected ObservableScalar<? extends Node> paneContent() {
        LinePane top = new LinePane(width, color), right = new LinePane(width, color);
        LinePane bottom = new LinePane(width, color), left = new LinePane(width, color);

        return new Layout(ObservableListImpl.createMutable(top, right, bottom, left)) {

            {
                dependsOn(width);
            }

            @Override
            protected Size determineSize(BoxConstraints constraints) {
                // TODO itt failolni kéne, ha kisebb
                return constraints.clamp(new Size(width.get().mul(TWO), width.get().mul(TWO)));
            }

            @Override
            protected void layout(Collection<?> updated) {
                Size s = bounds.get().size();
                Num w = width.get();

                top.bounds.set(new Rectangle(ORIGO, new Size(s.width(), w)));
                right.bounds.set(new Rectangle(new Point(s.width().sub(w), ZERO), new Size(w, s.height())));
                bottom.bounds.set(new Rectangle(new Point(ZERO, s.height().sub(w)), new Size(s.width(), w)));
                left.bounds.set(new Rectangle(ORIGO, new Size(w, s.height())));
            }
        }.asNodeObservable();
    }
}
