package ui10.geom;

public interface Num {

    double toDouble();

    Num ZERO = num(0);

    Num ONE = num(1);
    Num TWO = num(2);

    static Num num(int i) {
        return new FixedPointNumber(i * 1000);
    }

    Num add(Num n);

    Num sub(Num n);

    Num mul(Num n);

    Num div(Num n);

    static Num min(Num a, Num b) {
        return new FloatingPointNumber(Math.min(a.toDouble(), b.toDouble()));
    }

    static Num max(Num a, Num b) {
        return new FloatingPointNumber(Math.max(a.toDouble(), b.toDouble()));
    }

    default boolean isNegative() {
        return toDouble() < 0;
    }

}
