package ui10.controls;

import ui10.binding.ScalarProperty;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.image.Colors;
import ui10.layout.BoxConstraints;
import ui10.base.Element;
import ui10.base.LayoutContext2;
import ui10.base.Pane;
import ui10.decoration.css.CSSClass;
import ui10.graphics.ColorFill;
import ui10.graphics.TextNode;
import ui10.base.LayoutContext1;

import java.util.function.Consumer;

import static ui10.decoration.css.CSSClass.withClass;

public class TextField extends Pane {

    public final ScalarProperty<String> text = ScalarProperty.createWithDefault("TextField.text", "szöveg");
    public final ScalarProperty<Integer> caretPosition = ScalarProperty.createWithDefault("TextField.caretPosition", 0);

    private final TextNode textNode = withClass("text", new TextNode().text("mmmŰ"));
    private final ColorFill caret = withClass("caret", new ColorFill().color(Colors.BLACK));

    {
        attributes().add(new CSSClass("text-field"));
    }

    @Override
    public Element content() {
        return new Element() {
            @Override
            public void enumerateStaticChildren(Consumer<Element> consumer) {
                consumer.accept(textNode);
                consumer.accept(caret);
            }

            @Override
            public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
                return context.preferredSize(textNode, constraints);
            }

            @Override
            protected void performLayoutImpl(Shape shape, LayoutContext2 context) {
                context.placeElement(textNode, shape);

                Rectangle caretShape = new Rectangle(caretPosition.get(), 0, 1, shape.bounds().size().height()).
                        translate(shape.bounds().topLeft());
                context.placeElement(caret, caretShape);
            }

        };
    }
}
