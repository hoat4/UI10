package ui10.ui6.decoration;

import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.Attribute;
import ui10.ui6.Element;
import ui10.ui6.LayoutContext;
import ui10.ui6.decoration.css.CSSClass;
import ui10.ui6.decoration.css.CSSParser;
import ui10.ui6.decoration.css.Rule;

import java.util.function.Consumer;

public class Decorated extends Element.TransientElement {

    private final Element content;
    final CSSParser css;

    public Decorated(Element content, CSSParser css) {
        this.content = content;
        this.css = css;

        applyDecoration(content);
    }

    @Override
    public void enumerateLogicalChildren(Consumer<Element> consumer) {
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

    private void applyDecoration(Element element) {
        DecorationContext context = new DecorationContext();

        for (Attribute a : element.attributes()) {
            if (a instanceof CSSClass c) {
                Rule rule = css.rulesByClass.get(c.name);
                if (rule != null)
                    rule.apply1(element, context);
            }
        }

        element.enumerateLogicalChildren(this::applyDecoration);

        Element e = element;
        for (Attribute a : element.attributes()) {
            if (a instanceof CSSClass c) {
                Rule rule = css.rulesByClass.get(c.name);
                if (rule != null)
                    e = rule.apply2(e, context);
            }
        }

        if (e != element)
            element.replacement(e);
    }
}
