package ui10.ui6.decoration;

import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.*;
import ui10.ui6.decoration.css.CSSParser;
import ui10.ui6.decoration.css.CSSPseudoClass;
import ui10.ui6.decoration.css.Rule;
import ui10.ui6.layout.LayoutContext1;
import ui10.ui6.layout.LayoutContext2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CSSDecorator extends Element.TransientElement {

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
    protected Shape preferredShapeImpl(BoxConstraints constraints, LayoutContext1 context) {
        return content.preferredShape(constraints, context);
    }

    @Override
    protected void applyShapeImpl(Shape shape, LayoutContext2 context) {
        content.performLayout(shape, context);
    }

    private void applyOnRegularElement(Element element) {
        applySelf(element);

        if (element instanceof Pane p) {
            p.decorator = this::applyOnPaneContent;
        } else {
            element.enumerateStaticChildren(this::applyOnRegularElement);
            applyReplacements(element, element);
        }
    }

    private void applyOnPaneContent(Pane pane, Element paneContent) {
        applySelf(pane);
        applySelf(paneContent);

        paneContent.enumerateStaticChildren(this::applyOnRegularElement);

        DecorationContext context = new DecorationContext();
        Element e = paneContent;

        if (paneContent instanceof Pane p)
            p.decorator = this::applyOnPaneContent;
        else
            e = ruleOf(paneContent).apply2(e, context);
        e = ruleOf(pane).apply2(e, context);

        if (e != paneContent)
            paneContent.replacement(e);
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
        attributes.addAll(e.attributes());
        if (e == this.content)
            attributes.add(new CSSPseudoClass("root"));
        if (e instanceof Control c && c.focusContext != null && c.focusContext.focusedControl.get() == c)
            attributes.add(new CSSPseudoClass("focus"));

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
