package ui10.renderer.java2d;

import ui10.font.FontMetrics;
import ui10.font.TextStyle;

import java.awt.*;

import static ui10.renderer.java2d.J2DUtil.px2i;

public class AWTTextStyle implements TextStyle {

    public static final Canvas C = new Canvas();

    public final Font font;
    public final java.awt.FontMetrics fontMetrics;

    public static AWTTextStyle of(float size) {
        Font font = new Font("Segoe UI", 0, (int)size);
        if (size != (int)size)
            font = font.deriveFont((float)size);
        return new AWTTextStyle(font, C.getFontMetrics(font));
    }

    public AWTTextStyle(Font font, java.awt.FontMetrics fontMetrics) {
        this.font = font;
        this.fontMetrics = fontMetrics;
    }

    @Override
    public FontMetrics textSize(String text) {
        return new FontMetrics(px2i(fontMetrics.stringWidth(text)),
                px2i(fontMetrics.getAscent()), px2i(fontMetrics.getDescent()));
    }

    @Override
    public int height() {
        return px2i(fontMetrics.getHeight());
    }
}
