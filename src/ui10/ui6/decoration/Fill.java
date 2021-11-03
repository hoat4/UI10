package ui10.ui6.decoration;

import ui10.geom.Point;
import ui10.geom.shape.Shape;
import ui10.image.Color;
import ui10.layout.BoxConstraints;
import ui10.ui6.Element;
import ui10.ui6.LayoutContext;
import ui10.ui6.decoration.css.Length;
import ui10.ui6.graphics.LinearGradient;
import ui10.ui6.layout.LayoutResult;
import ui10.ui6.layout.Layouts;

import java.util.List;
import java.util.function.Consumer;

public interface Fill {

    Element makeElement(DecorationContext context);

    record ColorFill(Color color) implements Fill {
        @Override
        public Element makeElement(DecorationContext context) {
            return new ui10.ui6.graphics.ColorFill(color);
        }
    }

    record LinearGradientFill(PointSpec from, PointSpec to, List<Stop> stops) implements Fill {

        public record Stop(Color color, Length length) {
        }

        @Override
        public Element makeElement(DecorationContext context) {
            LinearGradient g = new LinearGradient();

            return new Layouts.SingleNodeLayout(g) {

                @Override
                protected LayoutResult preferredShapeImpl(BoxConstraints constraints) {
                    LayoutResult lr = g.preferredShape(constraints);
                    return new LayoutResult(lr.shape(), this, lr);
                }

                @Override
                protected void applyShapeImpl(Shape shape, LayoutContext layoutContext, List<LayoutResult> lr) {
                    context.parentSize = shape.bounds().size();
                    try {
                        int length = Point.distance(from.makePoint(context), to.makePoint(context));

                        g.start(from.makePoint(context));
                        g.end(to.makePoint(context));
                        g.stops.clear();
                        for (Stop stop : stops)
                            g.stops.add(new LinearGradient.Stop(stop.color, context.length(stop.length, length)));
                    } finally {
                        context.parentSize = null;
                    }

                    g.performLayout(shape, layoutContext, unwrap(lr));
                }

            };
        }

    }

}
