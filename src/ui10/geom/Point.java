package ui10.geom;

import static ui10.geom.NumericValue.number;

public record Point(NumericValue x, NumericValue y, NumericValue z) {

    public Point(int x, int y, int z) {
        this(number(x), number(y), number(z));
    }

    public static final Point ORIGO = new Point(0, 0, 0);
}
