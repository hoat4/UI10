package ui10.decoration.css;

/**
 * All values shifted left by 14 bits.
 */
public record Length(int px, int em, int relative) {
    public static Length zero() {
        return new Length(0, 0, 0);
    }

    public static Length percent(int percent) {
        return new Length(0, 0, (percent << 14) / 100);
    }

    public static Length em(int ems) {
        return new Length(0, ems << 14, 0);
    }
}
