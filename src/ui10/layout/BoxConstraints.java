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
        return new BoxConstraints(min.subtract(point), max.subtract(point));
    }

    public BoxConstraints subtract(Size size) {
        Objects.requireNonNull(size);
        return new BoxConstraints(min.subtract(size), max.subtract(size));
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
}
