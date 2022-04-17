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
            return new LinearGradient() {
                @Override
                protected void onShapeApplied(Shape shape) {
                    context.parentSize = shape.bounds().size();

                    try {
                        int length = Point.distance(
                                LinearGradientFill.this.from.makePoint(context),
                                LinearGradientFill.this.to.makePoint(context)
                        );

                        start(LinearGradientFill.this.from.makePoint(context));
                        end(LinearGradientFill.this.to.makePoint(context));
                        stops.clear();
                        for (LinearGradientFill.Stop stop : LinearGradientFill.this.stops)
                            stops.add(new LinearGradient.Stop(stop.color, context.length(stop.length, length)));
                    } finally {
                        context.parentSize = null;
                    }
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