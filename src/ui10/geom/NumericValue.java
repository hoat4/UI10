package ui10.geom;

public interface NumericValue {

    double toDouble();

    NumericValue ZERO = num(0);

    NumericValue ONE = num(1);

    static NumericValue num(int i) {
        return new FixedPointNumber(i*1000);
    }

    NumericValue add(NumericValue n);

    NumericValue sub(NumericValue n);

    NumericValue div(NumericValue n);

    static NumericValue min(NumericValue a, NumericValue b) {
        return new FloatingPointNumber(Math.min(a.toDouble(), b.toDouble()));
    }

    static NumericValue max(NumericValue a, NumericValue b) {
        return new FloatingPointNumber(Math.max(a.toDouble(), b.toDouble()));
    }
}
