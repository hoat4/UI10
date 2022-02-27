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

    public Fraction multiply(int multiplier) {
        return new Fraction(numerator * multiplier, denominator);
    }

    public Fraction divide(Fraction divisor) {
        return new Fraction(numerator * divisor.denominator, denominator * divisor.numerator);
    }

    public Fraction add(int i) {
        return new Fraction(numerator + i * denominator, denominator);
    }

    public int floor() {
        // TODO negatív esetek kezelése?
        return numerator / denominator;
    }

    public int ceil() {
        // TODO negatív esetek kezelése?
        return (numerator + denominator - 1) / denominator;
    }

    public int round() {
        return (numerator + denominator / 2) / denominator;
    }

    public static Fraction add(Fraction a, Fraction b) {
        if (a.denominator == b.denominator)
            return new Fraction(a.numerator + b.numerator, a.denominator);

        if (a.denominator % b.denominator == 0)
            // ilyeneket lehet hogy csak akkor kéne csinálni ha már túl nagy a nevező, mert az osztás lassú művelet
            return new Fraction(a.numerator + b.numerator * a.denominator / b.denominator, a.denominator);

        return new Fraction(a.numerator * b.denominator + b.numerator * a.denominator,
                a.denominator * b.denominator);
    }

    public static Fraction subtract(Fraction a, Fraction b) {
        return add(a, negate(b));
    }

    public static Fraction negate(Fraction f) {
        return new Fraction(-f.numerator, f.denominator);
    }

    public static Fraction of(double d, int denominator) {
        // round?
        return new Fraction((int) (d * denominator), denominator);
    }

    public static Fraction of(int value) {
        return new Fraction(value, 1);
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
