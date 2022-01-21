package ui10.geom;

public record Fraction(int numerator, int denominator) {

    public static final Fraction ZERO = new Fraction(0, 1);
    public static final Fraction HALF = new Fraction(1, 2);
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

    public float toFloat() {
        return (float) numerator / denominator;
    }

    public double toDouble() {
        return (double) numerator / denominator;
    }

    /**
     * @return {@link #WHOLE} - {@code this}
     */
    public Fraction oneMinus() {
        return new Fraction(denominator - numerator, denominator);
    }
}
