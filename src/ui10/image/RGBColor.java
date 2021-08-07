package ui10.image;

public record RGBColor(double red, double green, double blue, double alpha) implements Color {

    public static final RGBColor RED = new RGBColor(1, 0, 0, 1);
    public static final RGBColor GREEN = new RGBColor(0, 1, 0, 1);
    public static final RGBColor BLUE = new RGBColor(0, 0, 1, 1);
    public static final RGBColor BLACK = ofRGB(0x000000);

    public RGBColor {
        if (red < 0 || red > 1)
            throw new IllegalArgumentException("Color's red value (" + red + ") must be in the range 0.0-1.0" + this);
        if (green < 0 || green > 1)
            throw new IllegalArgumentException("Color's green value (" + green + ") must be in the range 0.0-1.0" + this);
        if (blue < 0 || blue > 1)
            throw new IllegalArgumentException("Color's blue value (" + blue + ") must be in the range 0.0-1.0: " + this);
        if (alpha < 0 || alpha > 1)
            throw new IllegalArgumentException("Color's alpha value (" + alpha + ") must be in the range 0.0-1.0: " + this);
    }

    public static RGBColor ofIntRGBA(int rgba) {
        return new RGBColor((rgba >>> 24) / 255.0, (rgba >>> 16 & 255) / 255.0,
                (rgba >>> 8 & 255) / 255.0, (rgba & 255) / 255.0);
    }

    public static RGBColor ofRGB(int rgb) {
        return new RGBColor((rgb >>> 16 & 255) / 255.0,
                (rgb >>> 8 & 255) / 255.0, (rgb & 255) / 255.0, 1);
    }

    public static RGBColor ofRGBShort(int rgb) {
        return new RGBColor((rgb >>> 8) / 15.0, (rgb >>> 4 & 15) / 15.0,
                (rgb & 15) / 15.0, 1);
    }

    public int toIntRGBA() {
        return (int) (red * 255 + 0.5) << 24 | (int) (green * 255 + 0.5) << 16 |
                (int) (blue * 255 + 0.5) << 8 | (int) (alpha * 255 + 0.5);
    }

    @Override
    public String toString() {
        // TODO alpha
        return String.format("#%02X%02X%02X", (int) (red*255+.5), (int) (green*255+.5), (int) (blue*255+.5));
    }
}
