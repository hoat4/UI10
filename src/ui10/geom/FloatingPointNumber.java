package ui10.geom;

public record FloatingPointNumber(double value) implements NumericValue {
    @Override
    public double toDouble() {
        return value;
    }

    @Override
    public NumericValue add(NumericValue n) {
        return new FloatingPointNumber(toDouble() + n.toDouble());
    }

    @Override
    public NumericValue sub(NumericValue n) {
        return new FloatingPointNumber(toDouble() - n.toDouble());
    }

    @Override
    public NumericValue div(NumericValue n) {
        return new FloatingPointNumber(toDouble() / n.toDouble());
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
