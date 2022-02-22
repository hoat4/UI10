package ui10.ui6.decoration.css;

import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.Attribute;
import ui10.ui6.Control;
import ui10.ui6.Element;
import ui10.ui6.Pane;
import ui10.ui6.decoration.DecorationContext;
import ui10.ui6.LayoutContext1;
import ui10.ui6.LayoutContext2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CSSDecorator extends Element {

    private final Element content;
    final CSSParser css;

    public CSSDecorator(Element content, CSSParser css) {
        this.content = content;
        this.css = css;

        applyOnRegularElement(content);
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

    private void applyOnRegularElement(Element element) {
        DecorationContext context = new DecorationContext();
        applySelf(element, context);

        if (element instanceof Pane p) {
            p.decorator = this::applyOnPaneContent;
        } else {
            element.enumerateStaticChildren(this::applyOnRegularElement);
            applyReplacements(element, element, context);
        }
    }

    private void applyOnPaneContent(Pane pane, Element paneContent) {
        DecorationContext context = new DecorationContext();

        applySelf(pane, context);
        applySelf(paneContent, context);

        paneContent.enumerateStaticChildren(this::applyOnRegularElement);

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
        List<Attribute> attributes = new ArrayList<>();
        attributes.addAll(e.attributes());
        if (e == this.content)
            attributes.add(new CSSPseudoClass("root"));
        if (e instanceof Control c && c.focusContext != null && c.focusContext.focusedControl.get() == c)
            attributes.add(new CSSPseudoClass("focus"));

        Rule rule = new Rule();

        for (Attribute a : attributes) {
            Rule r = css.rules.get(a);
            if (r != null) {
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
