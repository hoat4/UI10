package ui10.layout;

import ui10.geom.Point;
import ui10.geom.Size;

import javax.swing.*;
import java.util.Objects;

public record BoxConstraints(Size min, Size max) {
    public static BoxConstraints fixed(Size size) {
        return new BoxConstraints(size, size);
    }

    public BoxConstraints subtract(Point point) {
        Objects.requireNonNull(point);
        return new BoxConstraints(min.subtract(point), max.subtract(point));
    }

    public BoxConstraints withMinimum(Size min) {
        return new BoxConstraints(min, max);
    }
}
