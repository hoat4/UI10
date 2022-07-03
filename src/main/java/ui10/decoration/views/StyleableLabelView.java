package ui10.decoration.views;

import ui10.base.*;
import ui10.controls.TextView;
import ui10.controls.TextElement;
import ui10.decoration.Fill;
import ui10.decoration.Style;
import ui10.font.TextStyle;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.layout.Layouts;
import ui10.layout.LinearLayout;
import ui10.layout.RectangularLayout;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static ui10.layout.Layouts.horizontally;

// TODO text-align legyen állítható CSS-ből
// .label
public class StyleableLabelView extends StyleableView<TextView, StyleableLabelView.TextViewStyle> implements ui10.binding7.InvalidationListener {

    private final TextElement textElement = new TextElement(); // .label-text
    private final TextElement textNodeSel = new TextElement();
    private final TextElement textNodeAfterSel = new TextElement();
    private Element selectionFill;
    private final TextViewContent content = new TextViewContent();

    public StyleableLabelView(TextView model) {
        super(model);
    }

    @Override
    protected void validateImpl() {
        if (model.dirtyProperties().contains(TextView.TextViewProperty.TEXT) ||
                model.dirtyProperties().contains(TextView.TextViewProperty.SELECTION)) {
            if (model.selection() == null) {
                textElement.text(model.text());
                textNodeSel.text(null);
                textNodeAfterSel.text(null);
            } else {
                textElement.text(model.text().substring(0, p(model.selection().begin())));
                textNodeSel.text(model.text().substring(p(model.selection().begin()), p(model.selection().end())));
                textNodeAfterSel.text(model.text().substring(p(model.selection().end())));
            }
        }

        selectionFill = decoration().selectedPart().background().makeElement(decoration().decorationContext());
        textElement.fill(decoration().nonSelectedPart().foreground().makeElement(decoration().decorationContext()));
        textNodeSel.fill(decoration().selectedPart().foreground().makeElement(decoration().decorationContext()));
        textNodeAfterSel.fill(decoration().nonSelectedPart().foreground().makeElement(decoration().decorationContext()));
        textElement.textStyle(decoration().textStyle());
        textNodeSel.textStyle(decoration().textStyle());
        textNodeAfterSel.textStyle(decoration().textStyle());
    }

    private static int p(ContentEditable.ContentPoint p) {
        return ((TextView.StringContentPoint) p).characterOffset();
    }

    @Override
    protected Element contentImpl() {
        return switch (decoration().textAlign()) {
            case LEFT -> content;
            case CENTER -> Layouts.halign(Layouts.HorizontalAlignment.CENTER, content);
            case RIGHT -> Layouts.halign(Layouts.HorizontalAlignment.RIGHT, content);
        };
    }

    @Setup
    public void textColorChanged() {
        textElement.fill(decoration().textFill().makeElement(null));
    }

    @Setup
    public void textStyleChanged() {
        textElement.textStyle(decoration().textStyle());
    }

    public void textAlignChanged() {
        invalidateContainer();
    }

    @Override
    public ContentEditable.ContentPoint pickPosition(Point p) {
        p = p.subtract(origin());
        int i = textElement.textLayout().pickTextPos(p);
        int l1 = textElement.text().length();
        if (model.selection() == null || i < l1)
            return new TextView.StringContentPoint(i, this);

        p = p.subtract(textElement.textLayout().metrics().width(), 0);
        i = textNodeSel.textLayout().pickTextPos(p);

        int l2 = textNodeSel.text().length();
        if (i < l2)
            return new TextView.StringContentPoint(l1 + i, this);

        p = p.subtract(textNodeSel.textLayout().metrics().width(), 0);
        i = textNodeAfterSel.textLayout().pickTextPos(p);
        return new TextView.StringContentPoint(l1 + l2 + i, this);
    }

    @Override
    public Shape shapeOfSelection(ContentEditable.ContentRange<?> range) {
        assert range.begin().element() == model;
        assert range.end().element() == model;
        int beginPos = ((TextView.StringContentPoint) range.begin()).characterOffset();
        int endPos = ((TextView.StringContentPoint) range.end()).characterOffset();
        int x = textElement.textStyle().textSize(model.text().substring(0, beginPos)).width();
        int w = textElement.textStyle().textSize(model.text().substring(beginPos, endPos)).width();
        return new Rectangle(x, 0, w, textElement.textStyle().height()).translate(origin());
    }

    private class TextViewContent extends RectangularLayout {

        private final LinearLayout hbox = horizontally(textElement, textNodeSel, textNodeAfterSel);

        @Override
        public void enumerateChildren(Consumer<Element> consumer) {
            consumer.accept(hbox);
            consumer.accept(selectionFill);
        }

        @Override
        public Size preferredSize(BoxConstraints constraints, LayoutContext1 context) {
            Size caretWidth = new Size(1, 0);
            return context.preferredSize(hbox, constraints.subtract(caretWidth)).add(caretWidth);
        }

        @Override
        protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
            if (model.selection() != null) {
                int begin = p(model.selection().begin());
                int end = p(model.selection().end());

                int beginX = textPosToX(begin), endX = textPosToX(end);

                Rectangle r = Rectangle.of(size.withWidth(beginX));
                r = Rectangle.of(r.topLeft().addX(beginX), r.bottomLeft().addX(endX));
                placer.accept(selectionFill, r);
            }
            placer.accept(hbox, Rectangle.of(size));
        }

        private int textPosToX(Integer x) {
            return textElement.textStyle().textSize(model.text().substring(0, x)).width();
        }

    }

    public interface TextViewStyle extends Style {

        TextAlign textAlign();

        Fill textFill();

        TextStyle textStyle();

        TextViewPartDecoration nonSelectedPart();

        TextViewPartDecoration selectedPart();

        interface TextViewPartDecoration {

            Fill foreground();

            Fill background();
        }
    }
}
