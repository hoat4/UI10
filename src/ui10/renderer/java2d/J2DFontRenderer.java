package ui10.renderer.java2d;

import ui10.font.FontContext;
import ui10.font.FontMetrics;
import ui10.font.FontRenderer;
import ui10.geom.FloatingPointNumber;

import java.awt.*;
import java.awt.font.LineMetrics;

public class J2DFontRenderer implements FontRenderer {

    final Component awtComponent;

    public J2DFontRenderer(Component awtComponent) {
        this.awtComponent = awtComponent;
    }

    public Font toAWTFont(FontContext f) {
        Font font = new Font(Font.DIALOG, 0, (int)f.size().toDouble());
        if (f.size().toDouble() != (int)f.size().toDouble())
            font = font.deriveFont((float)f.size().toDouble());
        return font;
    }

    @Override
    public FontMetrics measure(String text, FontContext font) {
        java.awt.FontMetrics fm = awtComponent.getFontMetrics(toAWTFont(font));
        return new FontMetrics(new FloatingPointNumber(fm.stringWidth(text)),
                new FloatingPointNumber(fm.getAscent()), new FloatingPointNumber(fm.getDescent()));
    }
}
