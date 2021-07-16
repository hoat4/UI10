package ui10.geom;

import java.util.function.BinaryOperator;

public record FixedPointNumber(int value) implements NumericValue {

    @Override
    public double toDouble() {
        return value / 1000.0;
    }

    @Override
    public NumericValue add(NumericValue n) {
        return apply(n, Integer::sum, Double::sum);
    }

    @Override
    public NumericValue sub(NumericValue n) {
        return apply(n, (a, b) -> a - b, (a, b) -> a - b);
    }

    @Override
    public NumericValue div(NumericValue n) {
        return apply(n, (a, b)->a*1000/b, (a, b)->a/b);
    }

    private NumericValue apply(NumericValue other, BinaryOperator<Integer> intOp, BinaryOperator<Double> doubleOp) {
        if (other instanceof FixedPointNumber f)
            return new FixedPointNumber(intOp.apply(value, f.value));
        else
            return new FloatingPointNumber(doubleOp.apply(toDouble(), other.toDouble()));
    }
}
