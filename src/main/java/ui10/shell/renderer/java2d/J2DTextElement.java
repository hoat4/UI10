package ui10.shell.renderer.java2d;

import ui10.base.ContentEditable;
import ui10.base.LayoutContext1;
import ui10.base.LayoutContext2;
import ui10.controls.TextElement;
import ui10.controls.TextView;
import ui10.font.FontMetrics;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.graphics.TextLayout;
import ui10.layout.BoxConstraints;

import java.awt.Graphics2D;
import java.util.Objects;

public class J2DTextElement extends J2DRenderableElement<TextElement> implements TextElement.TextView {

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
    public TextLayout textLayout() {
        return textLayout;
    }

    @Override
    public ContentEditable.ContentPoint pickPosition(Point point) {
        int p = textLayout.pickTextPos(point.subtract(origin()));
        return new TextView.StringContentPoint(p, node);
    }

    @Override
    public Shape shapeOfSelection(ContentEditable.ContentRange<?> range) {
        assert range.begin().element() == node;
        assert range.end().element() == node;
        int beginPos = ((TextView.StringContentPoint) range.begin()).characterOffset();
        int endPos = ((TextView.StringContentPoint) range.end()).characterOffset();
        int x = node.textStyle().textSize(node.text().substring(0, beginPos)).width();
        int w = node.textStyle().textSize(node.text().substring(beginPos, endPos)).width();
        return new Rectangle(x, 0, w, node.textStyle().height()).translate(origin());
    }
}
