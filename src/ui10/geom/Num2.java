package ui10.geom;

public class Num2 {

    private final int value;

    private Num2(int value) {
        this.value = value;
    }

    public static Num2 of(int value) {
        return new Num2(value << 10);
    }

    public Num2 multiply(Fraction fraction) {
        return new Num2(value* fraction.numerator()/ fraction.denominator());
    }
}
