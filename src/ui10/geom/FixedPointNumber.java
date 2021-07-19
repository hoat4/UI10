package ui10.geom;

import java.util.function.BinaryOperator;

public record FixedPointNumber(int value) implements Num {

    @Override
    public double toDouble() {
        return value / 1000.0;
    }

    @Override
    public Num add(Num n) {
        return apply(n, Integer::sum, Double::sum);
    }

    @Override
    public Num sub(Num n) {
        return apply(n, (a, b) -> a - b, (a, b) -> a - b);
    }

    @Override
    public Num div(Num n) {
        return apply(n, (a, b) -> a * 1000 / b, (a, b) -> a / b);
    }

    private Num apply(Num other, BinaryOperator<Integer> intOp, BinaryOperator<Double> doubleOp) {
        if (other instanceof FixedPointNumber f)
            return new FixedPointNumber(intOp.apply(value, f.value));
        else
            return new FloatingPointNumber(doubleOp.apply(toDouble(), other.toDouble()));
    }

    @Override
    public String toString() {
        if (value % 1000 == 0)
            return Integer.toString(value / 1000);
        else
            return Double.toString(toDouble());
    }
}
