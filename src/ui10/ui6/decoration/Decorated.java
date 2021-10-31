package ui10.ui6.decoration;

import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.Attribute;
import ui10.ui6.Element;
import ui10.ui6.LayoutContext;

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
        element.enumerateLogicalChildren(this::applyDecoration);

        Element e = element;
        for (Attribute a : element.attributes()) {
            if (a instanceof CSSClass c) {
                Rule rule = css.rulesByClass.get(c.name);
                e = rule.apply(e, new DecorationContext());
            }
        }

        if (e != element)
            element.replacement(e);
    }
}
