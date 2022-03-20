package ui10.decoration.css;

import ui10.base.*;
import ui10.binding2.Property;
import ui10.decoration.DecorationContext;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.Objects;
import java.util.function.Consumer;

public class CSSDecorator extends TransientElement {

    public static final Property<CSSDecorator> DECORATOR_PROPERTY = new Property<>();

    final Element content;
    final CSSParser css;

    public CSSDecorator(Element content, CSSParser css) {
        Objects.requireNonNull(content);

        this.content = content;
        this.css = css;

        setProperty(DECORATOR_PROPERTY, this);
        // ???
        content.initParent(this);
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

    public void applySelf(Element element, DecorationContext context) {
        ruleOf(element).apply1(element, context);
    }

    public void applyReplacements(Element element, DecorationContext context, Element logicalParent) {
        Rule rule;
        if (logicalParent instanceof Pane p)
            rule = ruleOf(p);
        else if (element instanceof Pane)
            return;
        else
            rule = ruleOf(element);

        Element e = rule.apply2(element, context);

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
