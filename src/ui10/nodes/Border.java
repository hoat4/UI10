package ui10.nodes;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.image.Colors;
import ui10.image.Fill;
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
        ObservableScalar<Integer> w = style.map(s -> s == null ? 0 : s.width);
        Padding p = new Padding(w, content);
        StrokedRectanglePane rect = new StrokedRectanglePane(w,
                style.map(s -> s == null ? Colors.TRANSPARENT : s.fill));
        rect.radius.bindTo(style.map(b -> b == null ? 0 : b.radius));
        return ObservableScalar.ofConstant(new StackPane(p, rect));
    }

    public static record BorderStyle(int width, Fill fill, int radius) {
    }
}
