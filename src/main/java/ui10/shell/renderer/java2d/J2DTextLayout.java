package ui10.shell.renderer.java2d;

import ui10.font.FontMetrics;
import ui10.geom.Point;
import ui10.graphics.TextLayout;

public class J2DTextLayout implements TextLayout {

    private final String text;
    private final AWTTextStyle textStyle;

    public J2DTextLayout(String text, AWTTextStyle textStyle) {
        this.text = text;
        this.textStyle = textStyle;
    }

    @Override
    public FontMetrics metrics() {
        return textStyle.textSize(text);
    }

    @Override
    public int pickTextPos(Point p) {
        int x = p.x();

        int prevW = 0;
        for (int i = 1; i <= text.length(); i++) {
            int w = textStyle.textSize(text.substring(0, i)).width();
            int mid = (prevW + w * 2) / 3;

            if (mid >= x)
                return i - 1;

            prevW = w;
        }
        return text.length();
    }
}
