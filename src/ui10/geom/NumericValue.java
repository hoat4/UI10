package ui10.geom;

public interface NumericValue {

    double toDouble();

    NumericValue ZERO = number(0);

    NumericValue ONE = number(1);

    static NumericValue number(int i) {
        return new FixedPointNumber(i*1000);
    }

    NumericValue sub(NumericValue n);
}
