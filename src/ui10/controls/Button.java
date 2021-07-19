package ui10.controls;

import ui10.binding.ScalarProperty;
import ui10.image.RGBColor;
import ui10.node.Node;
import ui10.nodes.*;

import static ui10.geom.Num.num;

public class Button extends Control {

    private String text;

    public Button() {
    }

    public Button(String text) {
        this.text = text;
    }

    public ScalarProperty<String> text() {
        return property((Button n) -> n.text, (n, v) -> n.text = v);
    }

    @Override
    protected Node makeReplacement() {
        TextNode textNode = new TextNode(text);
        textNode.text().bindTo(text());
        return new StackPane(
                new RectangleNode(RGBColor.GREEN),
                new Padding(num(20), new Centered(textNode))
        );
    }
}
