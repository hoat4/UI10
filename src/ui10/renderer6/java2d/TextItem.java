package ui10.renderer6.java2d;

import ui10.renderer.java2d.AWTTextStyle;
import ui10.ui6.Element;
import ui10.ui6.RenderableElement;
import ui10.ui6.graphics.TextNode;

import java.awt.Graphics2D;
import java.util.Objects;

public class TextItem extends Item<TextNode> {

    private Element prevFill;
    private Item<?> fill;

    public TextItem(J2DRenderer renderer, TextNode node) {
        super(renderer, node);
    }

    @Override
    protected void validateImpl() {
        Objects.requireNonNull(node.fill());
        Objects.requireNonNull(node.text());
        Objects.requireNonNull(node.textStyle());

        if (!Objects.equals(prevFill, node.fill())) {
            prevFill = node.fill();
            fill = renderer.makeItem(RenderableElement.of(node.fill()));
        }

        // TODO cache textlayout vagy glyphvector vagy amit kell
    }

    @Override
    protected void drawImpl(Graphics2D g) {
        AWTTextStyle textStyle = (AWTTextStyle) node.textStyle();
        g.setFont(textStyle.font);
        g.setPaint(fill.asPaint());
        g.drawString(node.text(), shape.getBounds().x, shape.getBounds().y+textStyle.fontMetrics.getAscent());
    }

}
