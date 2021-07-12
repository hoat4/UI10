package ui10.geom;

public record FixedPointNumber(int value) implements NumericValue {

    @Override
    public double toDouble() {
        return value / 1000.0;
    }

    @Override
    public NumericValue sub(NumericValue n) {
        if (n instanceof FixedPointNumber f)
            return new FixedPointNumber(value-f.value);
        else
            return new FloatingPointNumber(toDouble() - n.toDouble());
    }
}
