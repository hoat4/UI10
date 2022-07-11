package ui10.decoration;

import ui10.base.*;
import ui10.binding9.OVal;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.css.CSSProperty;
import ui10.decoration.css.Length;
import ui10.decoration.css.Rule;
import ui10.decoration.views.StyleableView;
import ui10.geom.Insets;
import ui10.geom.Radiuses;
import ui10.geom.Size;
import ui10.geom.shape.RoundedRectangle;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class StyleableContainer extends LayoutElement {

    private final OVal<Element> content = new OVal<>();
    protected Rule rule;
    protected ElementMirrorImpl elementMirror;
    protected CSSDecorator css;
    protected DecorationContext decorContext;

    @OneTimeInit
    private void initElementMirror()  {
        css = lookup(CSSDecorator.class);
        elementMirror = new ElementMirrorImpl(this);
        rule = css.ruleOf(elementMirror);
        Objects.requireNonNull(rule);
        decorContext = new DecorationContext(this, findEmSize(this, css));
    }

    @RepeatedInit(100)
    private void initContent() {
        Element c = contentImpl();
        if (c == null)
            throw new NullPointerException(getClass().getName()+"::contentImpl returned null: "+this);
        content.set(c);
    }

    protected abstract Element contentImpl();

    @Override
    protected void enumerateChildren(Consumer<Element> consumer) {
        consumer.accept(content.get());
    }

    @Override
    protected Size preferredSize(BoxConstraints constraints, LayoutContext1 context) {
        int marginLeft = len(CSSProperty.MARGIN_LEFT_DCB_INDEX),
                marginRight = len(CSSProperty.MARGIN_RIGHT_DCB_INDEX),
                marginTop = len(CSSProperty.MARGIN_TOP_DCB_INDEX),
                marginBottom = len(CSSProperty.MARGIN_BOTTOM_DCB_INDEX);
        constraints = constraints.subtract(new Size(marginLeft + marginRight, marginTop + marginBottom));

        int minWidth = len(CSSProperty.MIN_WIDTH_INDEX), minHeight = len(CSSProperty.MIN_HEIGHT_INDEX);
        constraints = constraints.withMinimum(Size.max(constraints.min(), new Size(minWidth, minHeight)));

        int topLeftRadius = len(CSSProperty.TOP_LEFT_CORNER_RADIUS_INDEX),
                topRightRadius = len(CSSProperty.TOP_RIGHT_CORNER_RADIUS_INDEX),
                bottomLeftRadius = len(CSSProperty.BOTTOM_LEFT_CORNER_RADIUS_INDEX),
                bottomRightRadius = len(CSSProperty.BOTTOM_RIGHT_CORNER_RADIUS_INDEX);

        Size minSize = Size.max(constraints.min(), new Size(
                Math.max(topLeftRadius, bottomLeftRadius) +
                        Math.max(topRightRadius, bottomRightRadius),
                Math.max(topLeftRadius, topRightRadius) +
                        Math.max(bottomLeftRadius, bottomRightRadius)
        ));
        constraints = constraints.withMinimum(Size.max(constraints.min(), minSize));

        int topBorder = len(CSSProperty.borderTop, BorderSpec::len),
                rightBorder = len(CSSProperty.borderRight, BorderSpec::len),
                bottomBorder = len(CSSProperty.borderBottom, BorderSpec::len),
                leftBorder = len(CSSProperty.borderLeft, BorderSpec::len);
        constraints = constraints.subtract(new Size(leftBorder + rightBorder, topBorder + bottomBorder));

        int paddingTop = len(CSSProperty.PADDING_TOP_INDEX),
                paddingRight = len(CSSProperty.PADDING_RIGHT_INDEX),
                paddingBottom = len(CSSProperty.PADDING_BOTTOM_INDEX),
                paddingLeft = len(CSSProperty.PADDING_LEFT_INDEX);
        constraints = constraints.subtract(new Size(paddingLeft + paddingRight, paddingTop + paddingBottom));

        return context.preferredSize(content.get(), constraints).add(new Size(
                marginLeft + leftBorder + paddingLeft + paddingRight + rightBorder + marginRight,
                marginTop + topBorder + paddingTop + paddingBottom + bottomBorder + marginBottom
        ));
    }

    private int len(int propIndex) {
        Length l = (Length) rule.dcbProps[propIndex];
        return l == null ? 0 : decorContext.length(l);
    }

    private <T> int len(CSSProperty<T> prop, Function<T, Length> f) {
        T t = rule.get(prop);
        return t == null ? 0 : decorContext.length(f.apply(t));
    }

    private <T> int len(T t, Function<T, Length> f) {
        return t == null ? 0 : decorContext.length(f.apply(t));
    }

    @Override
    protected void performLayout(Shape shape, LayoutContext2 context) {
        if ((rule.dcbMask & CSSProperty.MARGIN_MASK) != 0) {
            int marginLeft = len(CSSProperty.MARGIN_LEFT_DCB_INDEX),
                    marginRight = len(CSSProperty.MARGIN_RIGHT_DCB_INDEX),
                    marginTop = len(CSSProperty.MARGIN_TOP_DCB_INDEX),
                    marginBottom = len(CSSProperty.MARGIN_BOTTOM_DCB_INDEX);
            if (marginTop != 0 || marginRight != 0 || marginBottom != 0 || marginLeft != 0)
                shape = shape.bounds().withInnerInsets(new Insets(marginTop, marginRight, marginBottom, marginLeft)).
                        intersectionWith(shape);
        }

        Shape roundedShape;
        int topLeftRadius = 0, topRightRadius = 0, bottomLeftRadius = 0, bottomRightRadius = 0;
        if ((rule.dcbMask & CSSProperty.CORNER_RADIUS_MASK) != 0) {
            topLeftRadius = len(CSSProperty.TOP_LEFT_CORNER_RADIUS_INDEX);
            topRightRadius = len(CSSProperty.TOP_RIGHT_CORNER_RADIUS_INDEX);
            bottomLeftRadius = len(CSSProperty.BOTTOM_LEFT_CORNER_RADIUS_INDEX);
            bottomRightRadius = len(CSSProperty.BOTTOM_RIGHT_CORNER_RADIUS_INDEX);
            if (topLeftRadius != 0 || topRightRadius != 0 || bottomLeftRadius != 0 || bottomRightRadius != 0)
                roundedShape = new RoundedRectangle(shape.bounds(), topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius);
            else
                roundedShape = shape;
        } else
            roundedShape = shape;

        if ((rule.dcbMask & CSSProperty.BORDER_MASK) != 0) {
            BorderSpec topBorder = rule.get(CSSProperty.borderTop),
                    rightBorder = rule.get(CSSProperty.borderRight),
                    bottomBorder = rule.get(CSSProperty.borderBottom),
                    leftBorder = rule.get(CSSProperty.borderLeft);
            if (topBorder != null || rightBorder != null || bottomBorder != null || leftBorder != null) {
                Fill anyBorderFill = null;
                boolean uniformFill = true;
                int top = 0, right = 0, bottom = 0, left = 0;
                if (topBorder != null) {
                    anyBorderFill = topBorder.fill();
                    top = decorContext.length(topBorder.len());
                }
                if (rightBorder != null) {
                    if (anyBorderFill == null)
                        anyBorderFill = rightBorder.fill();
                    else if (!anyBorderFill.equals(rightBorder.fill()))
                        uniformFill = false;
                    right = decorContext.length(rightBorder.len());
                }
                if (bottomBorder != null) {
                    if (anyBorderFill == null)
                        anyBorderFill = bottomBorder.fill();
                    else if (!anyBorderFill.equals(bottomBorder.fill()))
                        uniformFill = false;
                    bottom = decorContext.length(bottomBorder.len());
                }
                if (leftBorder != null) {
                    if (anyBorderFill == null)
                        anyBorderFill = leftBorder.fill();
                    else if (!anyBorderFill.equals(leftBorder.fill()))
                        uniformFill = false;
                    left = decorContext.length(leftBorder.len());
                }

                Radiuses radiuses = new Radiuses(topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius);
                if (uniformFill) {
                    Shape borderShape = BorderShape.make(shape,
                            new Insets(top, right, bottom, left),
                            radiuses);
                    context.placeElement(anyBorderFill.makeElement(decorContext), borderShape);
                } else {
                    if (topBorder != null) {
                        Shape topBorderShape = BorderShape.make(shape,
                                Insets.atTop(top),
                                radiuses);
                        context.placeElement(topBorder.fill().makeElement(decorContext), topBorderShape);
                    }
                    if (rightBorder != null) {
                        Shape rightBorderShape = BorderShape.make(shape,
                                Insets.atRight(right), radiuses);
                        context.placeElement(rightBorder.fill().makeElement(decorContext), rightBorderShape);
                    }
                    if (bottomBorder != null) {
                        Shape bottomBorderShape = BorderShape.make(shape,
                                Insets.atBottom(bottom), radiuses);
                        context.placeElement(bottomBorder.fill().makeElement(decorContext), bottomBorderShape);
                    }
                    if (leftBorder != null) {
                        Shape leftBorderShape = BorderShape.make(shape,
                                Insets.atLeft(left), radiuses);
                        context.placeElement(leftBorder.fill().makeElement(decorContext), leftBorderShape);
                    }
                }

                shape = new Insets(
                        len(topBorder, BorderSpec::len),
                        len(rightBorder, BorderSpec::len),
                        len(bottomBorder, BorderSpec::len),
                        len(leftBorder, BorderSpec::len)
                ).removeFrom(roundedShape);
            }
        }

        Fill background = rule.get(CSSProperty.background);
        if (background != null)
            context.placeElement(background.makeElement(decorContext), shape);

        if ((rule.dcbMask & CSSProperty.PADDING_MASK) != 0) {
            int paddingLeft = len(CSSProperty.PADDING_LEFT_INDEX),
                    paddingRight = len(CSSProperty.PADDING_RIGHT_INDEX),
                    paddingTop = len(CSSProperty.PADDING_TOP_INDEX),
                    paddingBottom = len(CSSProperty.PADDING_BOTTOM_INDEX);
            if (paddingLeft != 0 || paddingRight != 0 || paddingTop != 0 || paddingBottom != 0)
                shape = shape.bounds().withInnerInsets(new Insets(paddingTop, paddingRight, paddingBottom, paddingLeft));
        }

        context.placeElement(content.get(), shape);
    }

    protected Fill textFill() {
        Element e = this;
        while (true) {
            if (e instanceof StyleableView) {
                ElementMirrorImpl elementMirror = new ElementMirrorImpl((StyleableView<?>) e);
                Rule r = css.ruleOf(elementMirror);
                Fill l = r.get(CSSProperty.textColor);
                if (l != null)
                    return l;
            }
            e = e.parent();
        }
    }

    private static int findEmSize(Element e, CSSDecorator css) {
        int scale = 1 << 14;
        while (true) {
            if (e instanceof StyleableContainer) {
                ElementMirrorImpl elementMirror = new ElementMirrorImpl((StyleableContainer) e);
                Rule r = css.ruleOf(elementMirror);
                Length l = r.get(CSSProperty.fontSize);
                if (l != null) {
                    if (l.em() == 0 && l.relative() == 0)
                        return (scale >> 7) * (l.px() >> 7) >> 14;
                    else
                        scale = scale * (l.em() + l.relative() >> 7) >> 7;
                }
            }
            e = e.parent();
        }
    }
}
