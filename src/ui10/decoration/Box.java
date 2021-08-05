package ui10.decoration;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.geom.Num;
import ui10.image.Color;
import ui10.layout.Padding;
import ui10.layout.StackPane;
import ui10.nodes.Border;
import ui10.nodes.FilledPane;
import ui10.nodes.Node;
import ui10.nodes.WrapperPane;

public class Box extends WrapperPane {

    public final ScalarProperty<Color> background = ScalarProperty.create();
    public final ScalarProperty<Border.BorderStyle> borderStyle = ScalarProperty.create();
    public final ScalarProperty<Num> padding = ScalarProperty.create();

    public Box() {
    }

    public Box(Node content) {
        super(content);
    }

    public Box(ObservableScalar<Node> content) {
        super(content);
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        return ObservableScalar.ofConstant(new StackPane(
                new FilledPane(background),
                new Border(borderStyle, ObservableScalar.ofConstant(new Padding(padding, content)))
        ));
    }
}
