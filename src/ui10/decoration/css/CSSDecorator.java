package ui10.decoration.css;

import ui10.base.*;
import ui10.binding2.ChangeEvent;
import ui10.binding2.ElementEvent;
import ui10.binding2.Property;
import ui10.controls.Button;
import ui10.decoration.DecorationContext;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class CSSDecorator extends TransientElement {

    public static final Property<CSSDecorator> DECORATOR_PROPERTY = new Property<>(true);

    final Element content;
    final CSSParser css;

    public final List<PseudoClassProvider> pseudoClassProviders = new ArrayList<>(); // ha túl sok lesz belőle, alakítsuk át Mappé

    public CSSDecorator(Element content, CSSParser css) {
        Objects.requireNonNull(content);

        this.content = content;
        this.css = css;

        init();

        setProperty(DECORATOR_PROPERTY, this);
        content.initParent(this);
    }

    private void init() {
        pseudoClassProviders.add(new PseudoClassProvider("root", List.of(), e -> e == content));
        pseudoClassProviders.add(new PseudoClassProvider("focus", Control.FOCUSED_PROPERTY));
        pseudoClassProviders.add(new PseudoClassProvider("active", Button.PRESSED_PROPERTY));

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

    public void elementEvent(EnduringElement element, ElementEvent event) {
        if (event instanceof ChangeEvent<?> c) {
            if (pseudoClassProviders.stream().anyMatch(p->p.dependencies().contains(c.property()))) {
                System.out.println(element);
                element.invalidateDecoration();
            }
        }
    }

    public void applySelf(Element element, DecorationContext context) {
        ruleOf(element).apply1(element, context);
    }

    public void applyReplacements(Element element, DecorationContext context, Element logicalParent) {
        Rule rule;
        if (logicalParent instanceof ControlView<?> v)
            rule = ruleOf(v.model);
        else if (logicalParent instanceof Pane p)
            rule = ruleOf(p);
        else if (logicalParent instanceof ControlView<?> v)
            return;
        else if (element instanceof Pane || element instanceof ControlModel)
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
