package ui10.font;

import ui10.geom.Num;
import ui10.geom.Size;

public record FontMetrics(Num width, Num ascent, Num descent) {
    public Num height() {
        return ascent.add(descent);
    }

    public Size size() {
        return new Size(width(), height());
    }
}
