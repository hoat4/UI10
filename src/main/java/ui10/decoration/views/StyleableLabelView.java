package ui10.decoration.views;

import ui10.base.ContentEditable;
import ui10.base.Element;
import ui10.base.LayoutContext1;
import ui10.controls.TextAlign;
import ui10.controls.TextElement;
import ui10.controls.TextView;
import ui10.decoration.Fill;
import ui10.decoration.css.CSSProperty;
import ui10.decoration.css.ElementMirror;
import ui10.decoration.css.Length;
import ui10.decoration.css.Rule;
import ui10.font.TextStyle;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.graphics.FontWeight;
import ui10.layout.BoxConstraints;
import ui10.layout.Layouts;
import ui10.layout.LinearLayout;
import ui10.layout.RectangularLayout;
import ui10.shell.renderer.java2d.AWTTextStyle;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static ui10.decoration.css.Length.em;
import static ui10.layout.Layouts.horizontally;

// TODO text-align legyen állítható CSS-ből
// .label
public class StyleableLabelView extends StyleableView<TextView> {

    private final TextElement textElement = new TextElement(); // .label-text
    private final TextElement textNodeSel = new TextElement();
    private final TextElement textNodeAfterSel = new TextElement();
    private Element selectionFill;
    private final TextViewContent content = new TextViewContent();
    private Rule selectionRule;
    
    public StyleableLabelView(TextView model) {
        super(model);
    }

    @OneTimeInit(0)
    protected void init0() {
        ElementMirror selectionElementMirror = new ElementMirror() {

            @Override
            public boolean isPseudoElement(String pseudoElementName) {
                return pseudoElementName.equals("selection");
            }

            @Override
            public Optional<Integer> indexInSiblings() {
                return Optional.empty();
            }

            @Override
            public ElementMirror parent() {
                return elementMirror;
            }
        };
        selectionRule = css.ruleOf(selectionElementMirror);
    }

    @RepeatedInit(1)
    protected void init1() {
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

    @RepeatedInit(2)
    protected void init2() {
        Fill nonSelBG = dc-> Layouts.empty(), nonSelFG = rule.get(CSSProperty.textColor);
        Fill selBG = selectionRule.get(CSSProperty.background), selFG = selectionRule.get(CSSProperty.textColor);
        selectionFill = selBG.makeElement(decorContext);
        textElement.fill(nonSelFG.makeElement(decorContext));
        textNodeSel.fill(selFG.makeElement(decorContext));
        textNodeAfterSel.fill(nonSelFG.makeElement(decorContext));

        TextStyle textStyle = textStyle();
        textElement.textStyle(textStyle);
        textNodeSel.textStyle(textStyle);
        textNodeAfterSel.textStyle(textStyle);

        FontWeight fontWeight = rule.get(CSSProperty.fontWeight);
        textElement.fontWeight.set(fontWeight);
        textNodeSel.fontWeight.set(fontWeight);
        textNodeAfterSel.fontWeight.set(fontWeight);
    }

    private TextStyle textStyle() {
        Length len = rule.get(CSSProperty.fontSize);
        if (len == null)
            len = em(1);
        return AWTTextStyle.of(decorContext.length(len), false);
    }

    private static int p(ContentEditable.ContentPoint p) {
        return ((TextView.StringContentPoint) p).characterOffset();
    }

    @Override
    protected Element contentImpl() {
        return switch (rule.get(CSSProperty.textAlign)) {
            case LEFT -> content;
            case CENTER -> Layouts.halign(Layouts.HorizontalAlignment.CENTER, content);
            case RIGHT -> Layouts.halign(Layouts.HorizontalAlignment.RIGHT, content);
        };
    }

    @Override
    public ContentEditable.ContentPoint pickPosition(Point p) {
        p = p.subtract(origin());
        int i = textElement.textLayout().pickTextPos(p);
        int l1 = textElement.text().length();
        if (model.selection() == null || i < l1)
            return new TextView.StringContentPoint(i, model);

        p = p.subtract(textElement.textLayout().metrics().width(), 0);
        i = textNodeSel.textLayout().pickTextPos(p);

        int l2 = textNodeSel.text().length();
        if (i < l2)
            return new TextView.StringContentPoint(l1 + i, model);

        p = p.subtract(textNodeSel.textLayout().metrics().width(), 0);
        i = textNodeAfterSel.textLayout().pickTextPos(p);
        return new TextView.StringContentPoint(l1 + l2 + i, model);
    }

    @Override
    public Shape shapeOfSelection(ContentEditable.ContentRange<?> range) {
        assert range.begin().element() == model : range.begin().element() + ", " + model;
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
}
