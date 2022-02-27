package ui10.decoration;

import ui10.geom.Fraction;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.image.Color;
import ui10.layout.BoxConstraints;
import ui10.base.Element;
import ui10.base.LayoutContext2;
import ui10.decoration.css.Length;
import ui10.graphics.LinearGradient;
import ui10.base.LayoutContext1;
import ui10.layout.Layouts;
import ui10.layout.SingleNodeLayout;

import java.util.List;

import static ui10.layout.Layouts.withOpacity;

public interface Fill {

    Element makeElement(DecorationContext context);

    record ColorFill(Color color) implements Fill {
        @Override
        public Element makeElement(DecorationContext context) {
            return new ui10.graphics.ColorFill(color);
        }
    }

    record LinearGradientFill(PointSpec from, PointSpec to, List<Stop> stops) implements Fill {

        public record Stop(Color color, Length length) {
        }

        @Override
        public Element makeElement(DecorationContext context) {
            LinearGradient g = new LinearGradient();

            return new SingleNodeLayout(g) {

                @Override
                public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
                    return context.preferredSize(g, constraints);
                }

                @Override
                protected Shape computeContentShape(Shape containerShape, LayoutContext2 layoutContext) {
                    context.parentSize = containerShape.bounds().size();

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

                    return containerShape;
                }

            };
        }

    }

    class InterpolatedFill implements Fill {

        private final Fill a;
        private final Fill b;
        private final Fraction t;

        public InterpolatedFill(Fill a, Fill b, Fraction t) {
            this.a = a;
            this.b = b;
            this.t = t;
        }

        @Override
        public Element makeElement(DecorationContext context) {
            return Layouts.stack(withOpacity(a.makeElement(context), t.oneMinus()), withOpacity(b.makeElement(context), t));
        }
    }
}