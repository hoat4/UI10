package ui10.ui6.decoration;

import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.Attribute;
import ui10.ui6.Element;
import ui10.ui6.LayoutContext;
import ui10.ui6.Pane;
import ui10.ui6.decoration.css.CSSParser;
import ui10.ui6.decoration.css.CSSPseudoClass;
import ui10.ui6.decoration.css.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Decorated extends Element.TransientElement {

    private final Element content;
    final CSSParser css;

    public Decorated(Element content, CSSParser css) {
        this.content = content;
        this.css = css;

        applyOnRegularElement(content);
    }

    @Override
    public void enumerateStaticChildren(Consumer<Element> consumer) {
        consumer.accept(content);
    }

    @Override
    protected Shape preferredShapeImpl(BoxConstraints constraints) {
        return content.preferredShape(constraints);
    }

    @Override
    protected void applyShapeImpl(Shape shape, LayoutContext context) {
        content.applyShape(shape, context);
    }

    private void applyOnRegularElement(Element element) {
        applySelf(element);

        if (element instanceof Pane p) {
            p.decorator = (c, e) -> applyOnPaneContent(p, e);
        } else {
            element.enumerateStaticChildren(this::applyOnRegularElement);
            applyReplacements(element, element);
        }
    }

    private void applyOnPaneContent(Pane pane, Element element) {
        applySelf(pane);
        applySelf(element);

        element.enumerateStaticChildren(this::applyOnRegularElement);

        DecorationContext context = new DecorationContext();
        Element e = element;

        e = ruleOf(element).apply2(e, context);
        e = ruleOf(pane).apply2(e, context);

        if (e != element)
            element.replacement(e);
    }

    private void applySelf(Element element) {
        ruleOf(element).apply1(element, new DecorationContext());
    }

    private void applyReplacements(Element selectorElement, Element element) {
        Element e = ruleOf(selectorElement).apply2(element, new DecorationContext());

        if (e != element)
            element.replacement(e);
    }

    private Rule ruleOf(Element e) {
        List<Attribute> attributes = new ArrayList<>();
        if (e == this.content)
            attributes.add(new CSSPseudoClass("root"));
        attributes.addAll(e.attributes());

        Rule rule = new Rule();
        for (Attribute a : attributes) {
            Rule r = css.rules.get(a);
            if (r != null) {
                r.defaultsFrom(rule);
                rule = r;
            }
        }
        return rule;
    }
}
