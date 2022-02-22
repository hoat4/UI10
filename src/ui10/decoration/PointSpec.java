package ui10.ui6.decoration;

import ui10.geom.Point;
import ui10.ui6.decoration.css.Length;

public record PointSpec(Length x, Length y) {

    public Point makePoint(DecorationContext context) {
        return new Point(context.length(x, context.parentSize.width()), context.length(y, context.parentSize.height()));
    }
}
