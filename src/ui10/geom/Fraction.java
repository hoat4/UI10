package ui10.geom;

public record Fraction(int numerator, int denominator) {
    public static final Fraction WHOLE = new Fraction(1, 1);

    public boolean isNegative() {
        return numerator < 0 || denominator < 0;
    }

    public boolean isAboveOne() {
        return numerator > denominator;
    }

    public int interpolate(int a, int b) {
        if (isAboveOne() || isNegative())
            throw new IllegalArgumentException();

        // TODO overflow?
        return (a * (denominator - numerator) + b * numerator) / denominator;
    }

    public static Fraction of(double d, int denominator) {
        // round?
        return new Fraction((int) (d * denominator), denominator);
    }

    public double toDouble() {
        return (double) numerator / denominator;
    }
}
