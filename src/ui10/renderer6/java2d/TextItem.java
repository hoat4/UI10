package ui10.renderer6.java2d;

import ui10.image.Fill;
import ui10.renderer.java2d.AWTTextStyle;
import ui10.ui6.TextNode;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.Objects;

public class TextItem extends Item<TextNode> {

    private Fill prevFill;
    private Paint paint;

    public TextItem(J2DRenderer renderer, TextNode node) {
        super(renderer, node);
    }

    @Override
    protected void validate() {
        Objects.requireNonNull(node.fill());
        Objects.requireNonNull(node.text());
        Objects.requireNonNull(node.textStyle());

        if (!Objects.equals(prevFill, node.fill())) {
            prevFill = node.fill();
            paint = J2DUtil.asPaint(node.fill());
        }

        // TODO cache textlayout vagy glyphvector vagy amit kell
    }

    @Override
    protected void drawImpl(Graphics2D g) {
        AWTTextStyle textStyle = (AWTTextStyle) node.textStyle();
        g.setFont(textStyle.font);
        g.setPaint(paint);
        g.drawString(node.text(), 0, textStyle.fontMetrics.getAscent());
    }
}
