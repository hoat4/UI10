package ui10.shell.renderer.java2d;

import ui10.base.Element;
import ui10.base.RenderableElement;
import ui10.graphics.TextNode;

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
        Objects.requireNonNull(node.textFill());
        Objects.requireNonNull(node.text());
        Objects.requireNonNull(node.textStyle());

        if (!Objects.equals(prevFill, node.textFill())) {
            prevFill = node.textFill();
            fill = renderer.makeItem(RenderableElement.of(node.textFill()));
        }

        node.textLayout = new J2DTextLayout(node.text(), (AWTTextStyle) node.textStyle());

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
