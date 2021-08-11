package ui10.font;


import ui10.geom.Size;

public record FontMetrics(int width, int ascent, int descent) {
    public int height() {
        return ascent + descent;
    }

    public Size size() {
        return new Size(width(), height());
    }
}
