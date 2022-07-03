package ui10.decoration.css;

import ui10.base.*;
import ui10.decoration.BorderShape;
import ui10.decoration.BorderSpec;
import ui10.decoration.DecorationContext;
import ui10.decoration.Fill;
import ui10.geom.Insets;
import ui10.geom.Radiuses;
import ui10.geom.Size;
import ui10.geom.shape.RoundedRectangle;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.function.Consumer;
import java.util.function.Function;

public class DecorBox extends LayoutElement {

    public final Element content;
    public Rule rule;
    public final DecorationContext decorContext;

    public DecorBox(Element content, Rule rule, DecorationContext decorContext) {
        this.content = content;
        this.rule = rule;
        this.decorContext = decorContext;
    }

    @Override
    protected void enumerateChildren(Consumer<Element> consumer) {
        consumer.accept(content);
    }

    public void refresh() {
        invalidate(LayoutElementProperty.LAYOUT, LayoutElementProperty.CHILDREN);
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

        return context.preferredSize(content, constraints).add(new Size(
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

        context.placeElement(content, shape);
    }

    @Override
    public String toString() {
        return "DecorBox 0x"+Integer.toUnsignedString(hashCode(), 16)+" ("+content +')';
    }
}
