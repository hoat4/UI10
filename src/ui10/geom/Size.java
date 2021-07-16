package ui10.geom;

import java.util.function.UnaryOperator;

import static ui10.geom.NumericValue.num;

public record Size(NumericValue width, NumericValue height, NumericValue depth) {
    public static final Size ZERO = new Size(0, 0, 0);

    public Size(int width, int height, int depth) {
        this(num(width), num(height), num(depth));
    }

    public static Size max(Size a, Size b) {
        return new Size(NumericValue.max(a.width, b.width),
                NumericValue.max(a.height, b.height),
                NumericValue.max(a.depth, b.depth));
    }

    public Size subtract(Point point) {
        return new Size(width.sub(point.x()), height.sub(point.y()), depth.sub(point.z()));
    }
    public Size subtract(Size s) {
        return new Size(width.sub(s.width()), height.sub(s.height()), depth.sub(s.depth()));
    }

    public Size divide(NumericValue divisor) {
        return lanewise(n->n.div(divisor));
    }

    private Size lanewise(UnaryOperator<NumericValue> op) {
        return new Size(op.apply(width), op.apply(height), op.apply(depth));
    }
}
