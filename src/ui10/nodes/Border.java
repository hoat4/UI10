package ui10.nodes;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.geom.Num;
import ui10.image.Color;
import ui10.layout.Padding;
import ui10.layout.StackPane;

public class Border extends Pane {

    public final ScalarProperty<BorderStyle> style = ScalarProperty.create();
    public final ScalarProperty<Node> content = ScalarProperty.create();

    public Border() {
    }

    public Border(ObservableScalar<BorderStyle> borderStyle, ObservableScalar<Node> content) {
        this.style.bindTo(borderStyle);
        this.content.bindTo(content);
    }

    @Override
    protected ObservableScalar<Node> paneContent() {
        Padding p = new Padding(style.map(s -> s.width), content);
        StrokedRectanglePane rect = new StrokedRectanglePane(style.map(s -> s.width), style.map(s -> s.color));
        return ObservableScalar.ofConstant(new StackPane(p, rect));
    }

    public static record BorderStyle(Num width, Color color) {
    }
}
