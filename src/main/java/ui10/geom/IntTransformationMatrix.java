package ui10.geom;

// legyen inkább long?
public record IntTransformationMatrix(
        int m11, int m12, int m13,
        int m21, int m22, int m23,
        int divisorLog2
) implements Transformation {

    public static final IntTransformationMatrix IDENTITY = new IntTransformationMatrix(
            1, 0, 0,
            0, 1, 0,
            0
    );

    public IntTransformationMatrix concat(IntTransformationMatrix m) {
        int d = divisorLog2 + m.divisorLog2;
        if (d > 16)
            throw new IllegalArgumentException("loss of precision: " + this + " * " + m);
        return new IntTransformationMatrix(
                m11 * m.m11 + m12 * m.m21, m11 * m.m12 + m12 * m.m22, m11 * m.m13 + m12 * m.m21 + m13,
                m21 * m.m11 + m22 * m.m21, m21 * m.m12 + m22 * m.m22, m21 * m.m13 + m22 * m.m21 + m23,
                divisorLog2 + m.divisorLog2
        );
    }

    public Point transform(Point p) {
        return new Point(
                (p.x() * m11 + p.y() * m12 + m13) >> divisorLog2,
                (p.x() * m21 + p.y() * m22 + m23) >> divisorLog2
        );
    }
}
