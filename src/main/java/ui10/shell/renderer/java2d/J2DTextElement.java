package ui10.shell.renderer.java2d;

import ui10.base.LayoutContext1;
import ui10.base.LayoutContext2;
import ui10.controls.TextElement;
import ui10.font.FontMetrics;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.graphics.TextLayout;
import ui10.layout.BoxConstraints;

import java.awt.Graphics2D;
import java.util.Objects;

public class J2DTextElement extends J2DRenderableElement<TextElement> implements TextElement.TextElementListener, TextElement.TextView {

    private J2DRenderableElement<?> fill;
    private TextLayout textLayout;

    public J2DTextElement(J2DRenderer renderer, TextElement node) {
        super(renderer, node);
    }

    @Override
    protected void validateImpl() {
        Objects.requireNonNull(node.fill());
        Objects.requireNonNull(node.text());
        Objects.requireNonNull(node.textStyle());

        node.fill().initParent(this);
        fill = (J2DRenderableElement<?>) node.fill().renderableElement();

        textLayout = new J2DTextLayout(node.text(), (AWTTextStyle) node.textStyle());

        // TODO cache textlayout vagy glyphvector vagy amit kell
    }

    @Override
    protected void drawImpl(Graphics2D g) {
        AWTTextStyle textStyle = (AWTTextStyle) node.textStyle();
        g.setFont(textStyle.font);
        g.setPaint(fill.asPaint());
        g.drawString(node.text(), shape.getBounds().x, shape.getBounds().y + textStyle.fontMetrics.getAscent());
    }

    @Override
    protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        validateIfNeeded();
        FontMetrics fontMetrics = ((AWTTextStyle) node.textStyle()).textSize(node.text());
        return constraints.clamp(fontMetrics.size());
    }

    @Override
    protected void applyShape(Shape shape, LayoutContext2 context) {
        super.applyShape(shape, context);
        validateIfNeeded();
        LayoutContext2.ignoring(this).placeElement(fill, shape);
    }

    @Override
    public void textChanged() {
        invalidate();
    }

    @Override
    public void fillChanged() {
        invalidateRendererData();
    }

    @Override
    public TextLayout textLayout() {
        return textLayout;
    }
}
