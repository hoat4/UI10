package ui10.renderer.java2d;

import ui10.font.FontMetrics;
import ui10.font.TextStyle;
import ui10.geom.FloatingPointNumber;

import java.awt.*;

public class AWTTextStyle implements TextStyle {

    public static final Canvas C = new Canvas();

    private final Font font;
    private final java.awt.FontMetrics fontMetrics;

    public static AWTTextStyle of(float size) {
        Font font = new Font(Font.DIALOG, 0, (int)size);
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
        return new FontMetrics(new FloatingPointNumber(fontMetrics.stringWidth(text)),
                new FloatingPointNumber(fontMetrics.getAscent()), new FloatingPointNumber(fontMetrics.getDescent()));
    }
}
