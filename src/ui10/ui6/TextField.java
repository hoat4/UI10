package ui10.ui6;

import ui10.binding.ScalarProperty;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.image.RGBColor;
import ui10.layout.BoxConstraints;
import ui10.renderer.java2d.AWTTextStyle;

public class TextField extends DelegatingPersistentRenderableNode {

    public final ScalarProperty<String> text = ScalarProperty.createWithDefault("TextField.text", "szöveg");
    public final ScalarProperty<Integer> caretPosition = ScalarProperty.createWithDefault("TextField.caretPosition", 0);

    private final TextNode textNode = new TextNode().text("szöveg").
            textStyle(AWTTextStyle.of(16)).fill(RGBColor.BLACK);
    private final FilledRectangleNode caret = new FilledRectangleNode().fill(RGBColor.BLACK);

    @Override
    public Node content() {
        return new LayoutNode() {
            @Override
            public Size computeSize(BoxConstraints constraints) {
                return textNode.computeSize(constraints);
            }

            @Override
            public void applySize(Size size, LayoutContext layoutContext) {
                layoutContext.placeNode(textNode, Rectangle.of(size));
                layoutContext.placeNode(caret, new Rectangle(caretPosition.get(), 0, 1, size.height()));
            }
        };
    }
}
