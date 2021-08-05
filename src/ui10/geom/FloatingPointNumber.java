package ui10.geom;

public record FloatingPointNumber(double value) implements Num {
    @Override
    public double toDouble() {
        return value;
    }

    @Override
    public Num add(Num n) {
        return new FloatingPointNumber(toDouble() + n.toDouble());
    }

    @Override
    public Num sub(Num n) {
        return new FloatingPointNumber(toDouble() - n.toDouble());
    }

    @Override
    public Num mul(Num n) {
        return new FloatingPointNumber(toDouble() * n.toDouble());
    }

    @Override
    public Num div(Num n) {
        return new FloatingPointNumber(toDouble() / n.toDouble());
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
