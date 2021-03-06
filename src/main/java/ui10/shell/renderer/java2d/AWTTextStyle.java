package ui10.shell.renderer.java2d;

import ui10.font.FontMetrics;
import ui10.font.TextStyle;

import java.awt.*;

import static ui10.shell.renderer.java2d.J2DUtil.color;
import static ui10.shell.renderer.java2d.J2DUtil.px2i;

public class AWTTextStyle implements TextStyle {

    public static final Canvas C = new Canvas();

    public final Font font;
    public final java.awt.FontMetrics fontMetrics;
    private final double origSize;

    public static AWTTextStyle of(double size, boolean bold) {
        double origSize = size;
        size = origSize * 96 / 72;
        Font font = new Font(Font.DIALOG, bold ? Font.BOLD : Font.PLAIN, (int)size);
        if (size != (int)size)
            font = font.deriveFont((float)size);
        return new AWTTextStyle(font, C.getFontMetrics(font), origSize);
    }

    public TextStyle withBoldness(boolean b) {
        return of(origSize, b);
    }

    public AWTTextStyle(Font font, java.awt.FontMetrics fontMetrics, double origSize) {
        this.font = font;
        this.fontMetrics = fontMetrics;

//        for (Font f:GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts())
//            System.out.println(f.getName()+f.getStyle());
//        System.out.println(fontMetrics.getHeight());
        this.origSize = origSize;
    }

    @Override
    public FontMetrics textSize(String text) {
        return new FontMetrics(px2i(fontMetrics.stringWidth(text)) ,
                px2i(fontMetrics.getAscent()), px2i(fontMetrics.getDescent()));
    }

    @Override
    public int height() {
        return px2i(fontMetrics.getHeight());
    }
}
