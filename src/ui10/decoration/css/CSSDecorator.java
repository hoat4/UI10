package ui10.decoration.css;

import ui10.base.Element;
import ui10.base.LayoutContext1;
import ui10.base.LayoutContext2;
import ui10.base.Pane;
import ui10.binding2.Property;
import ui10.decoration.DecorationContext;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.Objects;
import java.util.function.Consumer;

public class CSSDecorator extends Element {

    public static final Property<CSSDecorator> DECORATOR_PROPERTY = new Property<>();

    final Element content;
    final CSSParser css;

    public CSSDecorator(Element content, CSSParser css) {
        Objects.requireNonNull(content);

        this.content = content;
        this.css = css;

        setProperty(DECORATOR_PROPERTY, this);
        applyOnRegularElement(content, this);
    }

    @Override
    public void enumerateStaticChildren(Consumer<Element> consumer) {
        consumer.accept(content);
    }

    @Override
    public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        return context.preferredSize(content, constraints);
    }

    @Override
    protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
        context.placeElement(content, shape);
    }

    public void applyOnRegularElement(Element element, Element parent) {
        element.logicalParent = parent;

        DecorationContext context = new DecorationContext();
        applySelf(element, context);
        element.initFromProps();

        if (element instanceof Pane p) {
            p.decorator = this::applyOnPaneContent;
        } else {
            element.enumerateStaticChildren(e -> {
                Objects.requireNonNull(e);
                applyOnRegularElement(e, element);
            });
            applyReplacements(element, element, context);
        }
    }

    private void applyOnPaneContent(Pane pane, Element paneContent) {
        paneContent.logicalParent = pane;

        DecorationContext context = new DecorationContext();

        applySelf(pane, context);
        applySelf(paneContent, context);

        paneContent.initFromProps();
        paneContent.enumerateStaticChildren(e -> applyOnRegularElement(e, paneContent));

        Element e = paneContent;

        if (paneContent instanceof Pane p)
            p.decorator = this::applyOnPaneContent;
        else
            e = ruleOf(paneContent).apply2(e, context);
        e = ruleOf(pane).apply2(e, context);

        if (e != paneContent)
            paneContent.replacement(e);
    }

    private void applySelf(Element element, DecorationContext context) {
        ruleOf(element).apply1(element, context);
    }

    private void applyReplacements(Element selectorElement, Element element, DecorationContext context) {
        Element e = ruleOf(selectorElement).apply2(element, context);

        if (e != element)
            element.replacement(e);
    }

    private Rule ruleOf(Element e) {
        Rule rule = new Rule();

        for (Rule r : css.rules) {
            if (r.selector.test(e, this)) {
                Rule r2 = new Rule();
                r2.defaultsFrom(r);
                r2.defaultsFrom(rule);
                rule = r2;
            }
        }

        rule.applyTransitionsOf(e);

        return rule;
    }
}
