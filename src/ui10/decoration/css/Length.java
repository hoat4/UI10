package ui10.ui6.decoration.css;

public record Length(int px, int em, int relative) {
    public static Length zero() {
        return new Length(0, 0, 0);
    }

    public static Length percent(int percent) {
        return new Length(0, 0, (percent << 14) / 100);
    }
}
