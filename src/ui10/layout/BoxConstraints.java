package ui10.layout;

import ui10.geom.Num;
import ui10.geom.Point;
import ui10.geom.Size;

import java.util.Objects;

public record BoxConstraints(Size min, Size max) {

    public static BoxConstraints fixed(Size size) {
        return new BoxConstraints(size, size);
    }

    public BoxConstraints subtract(Point point) {
        Objects.requireNonNull(point);
        if (max.width().sub(point.x()).isNegative() || max.width().sub(point.x()).isNegative())
            throw new IllegalArgumentException("couldn't subtract "+point+" from "+this);
        return new BoxConstraints(min.subtractOrClamp(point), max.subtract(point));
    }

    public BoxConstraints subtract(Size size) {
        Objects.requireNonNull(size);
        return new BoxConstraints(min.subtractOrClamp(size), max.subtract(size));
    }

    public BoxConstraints withMinimum(Size min) {
        return new BoxConstraints(min, max);
    }

    public Size clamp(Size size) {
        return new Size(
                Num.max(min.width(), Num.min(max.width(), size.width())),
                Num.max(min.height(), Num.min(max.height(), size.height()))
        );
    }

    public boolean contains(Size size) {
        return true; // TODO
    }

    public BoxConstraints withWidth(Num min, Num max) {
        return new BoxConstraints(new Size(min, this.min.height()), new Size(max, this.max.height()));
    }
    public BoxConstraints withHeight(Num min, Num max) {
        return new BoxConstraints(new Size(this.min.width(), min), new Size(this.max.width(), max));
    }
}
