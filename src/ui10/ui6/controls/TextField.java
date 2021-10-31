package ui10.ui6.controls;

import ui10.binding.ScalarProperty;
import ui10.geom.Rectangle;
import ui10.geom.shape.Shape;
import ui10.image.Colors;
import ui10.image.RGBColor;
import ui10.layout.BoxConstraints;
import ui10.renderer.java2d.AWTTextStyle;
import ui10.ui6.Element;
import ui10.ui6.LayoutContext;
import ui10.ui6.Pane;
import ui10.ui6.graphics.ColorFill;
import ui10.ui6.graphics.TextNode;

import java.util.function.Consumer;

public class TextField extends Pane {

    public final ScalarProperty<String> text = ScalarProperty.createWithDefault("TextField.text", "szöveg");
    public final ScalarProperty<Integer> caretPosition = ScalarProperty.createWithDefault("TextField.caretPosition", 0);

    private final TextNode textNode = new TextNode().text("mmmŰ").
            textStyle(AWTTextStyle.of(20f)).fill(new ColorFill(RGBColor.ofRGBShort(0x333)));
    private final ColorFill caret = new ColorFill().color(Colors.BLACK);

    @Override
    public Element content() {
        return new Element.TransientElement() {
            @Override
            public void enumerateLogicalChildren(Consumer<Element> consumer) {
                consumer.accept(textNode);
                consumer.accept(caret);
            }

            @Override
            protected Shape preferredShapeImpl(BoxConstraints constraints) {
                return textNode.preferredShape(constraints);
            }

            @Override
            protected void applyShapeImpl(Shape shape, LayoutContext context) {
                context.placeElement(textNode, shape);
                context.placeElement(caret, new Rectangle(caretPosition.get(), 0, 1, shape.bounds().size().height()).
                        translate(shape.bounds().topLeft()));
            }
        };
    }
}