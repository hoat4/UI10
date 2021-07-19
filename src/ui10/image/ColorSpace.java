package ui10.image;

public interface ColorSpace<C extends Color> {
    ColorSpace<RGBColor> SRGB = new ColorSpace<RGBColor>() {
    };
}
