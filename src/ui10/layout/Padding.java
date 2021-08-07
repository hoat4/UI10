package ui10.layout;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.geom.Num;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.nodes.Layout;
import ui10.nodes.Node;
import ui10.nodes.Pane;

import java.util.Collection;

import static ui10.binding.ObservableScalar.binding;

public class Padding extends Pane {

    public final ScalarProperty<Num> top = ScalarProperty.create();
    public final ScalarProperty<Num> right = ScalarProperty.create();
    public final ScalarProperty<Num> bottom = ScalarProperty.create();
    public final ScalarProperty<Num> left = ScalarProperty.create();
    public final ScalarProperty<Node> content = ScalarProperty.create();

    public Padding() {
    }

    public Padding(Num padding, Node content) {
        this(padding, padding, content);
    }

    public Padding(Num topBottom, Num leftRight, Node content) {
        this(topBottom, leftRight, topBottom, leftRight, content);
    }

    public Padding(Num top, Num right, Num bottom, Num left, Node content) {
        this.content.set(content);
        this.top.set(top);
        this.right.set(right);
        this.bottom.set(bottom);
        this.left.set(left);
    }

    public Padding(ObservableScalar<Num> padding, ObservableScalar<Node> content) {
        this(padding, padding, content);
    }

    public Padding(ObservableScalar<Num> topBottom, ObservableScalar<Num> leftRight, ObservableScalar<Node> content) {
        this(topBottom, leftRight, topBottom, leftRight, content);
    }

    public Padding(ObservableScalar<Num> top, ObservableScalar<Num> right,
                   ObservableScalar<Num> bottom, ObservableScalar<Num> left, ObservableScalar<Node> content) {
        this.content.bindTo(content);
        this.top.bindTo(top);
        this.right.bindTo(right);
        this.bottom.bindTo(bottom);
        this.left.bindTo(left);
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        ObservableScalar<Size> all = binding(left, right, top, bottom, (l, r, t, b) -> new Size(l.add(r), t.add(b)));
        return new Layout(content) {

            {
                dependsOn(all);
            }

            @Override
            protected Size determineSize(BoxConstraints constraints) {
                BoxConstraints c = new BoxConstraints(
                        constraints.min().subtractOrClamp(all.get()),
                        constraints.max().subtract(all.get()));

                return content.get().determineSize(c).add(all.get());
            }

            @Override
            protected void layout(Collection<?> updatedChildren) {
                content.get().bounds.set(Rectangle.of(bounds.get().size()).
                        withInsets(top.get(), right.get(), bottom.get(), left.get()));
            }
        }.asNodeObservable();
    }
}
