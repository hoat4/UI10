package ui10.decoration;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.geom.Insets;

import ui10.image.Fill;
import ui10.layout.Padding;
import ui10.layout.StackPane;
import ui10.nodes.Border;
import ui10.nodes.FilledRectanglePane;
import ui10.nodes.Node;
import ui10.nodes.WrapperPane;

public class Box extends WrapperPane {

    public final ScalarProperty<Fill> background = ScalarProperty.create();
    public final ScalarProperty<Border.BorderStyle> borderStyle = ScalarProperty.create();
    public final ScalarProperty<Insets> padding = ScalarProperty.createWithDefault(new Insets(0));

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
        ObservableScalar<Integer> cornerRadius = borderStyle.map(b -> b == null ? 0 : b.radius());

        Padding padding = new Padding();
        padding.bindToInsets(this.padding);
        padding.content.bindTo(content);

        FilledRectanglePane r = new FilledRectanglePane(background);
        r.radius.bindTo(cornerRadius);

        Border border = new Border(borderStyle, ObservableScalar.ofConstant(padding));
        return ObservableScalar.ofConstant(new StackPane(r, border));
    }
}
