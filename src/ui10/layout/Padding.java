package ui10.layout;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.geom.Insets;

import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.nodes.Layout;
import ui10.nodes.Node;
import ui10.nodes.Pane;

import java.util.Collection;

import static ui10.binding.ObservableScalar.binding;

public class Padding extends Pane {

    public final ScalarProperty<Integer> top = ScalarProperty.create();
    public final ScalarProperty<Integer> right = ScalarProperty.create();
    public final ScalarProperty<Integer> bottom = ScalarProperty.create();
    public final ScalarProperty<Integer> left = ScalarProperty.create();
    public final ScalarProperty<Node> content = ScalarProperty.create();

    public Padding() {
    }

    public Padding(int padding, Node content) {
        this(padding, padding, content);
    }

    public Padding(int topBottom, int leftRight, Node content) {
        this(topBottom, leftRight, topBottom, leftRight, content);
    }

    public Padding(int top, int right, int bottom, int left, Node content) {
        this.content.set(content);
        this.top.set(top);
        this.right.set(right);
        this.bottom.set(bottom);
        this.left.set(left);
    }

    public Padding(ObservableScalar<Integer> padding, ObservableScalar<Node> content) {
        this(padding, padding, content);
    }

    public Padding(ObservableScalar<Integer> topBottom, ObservableScalar<Integer> leftRight, ObservableScalar<Node> content) {
        this(topBottom, leftRight, topBottom, leftRight, content);
    }

    public Padding(ObservableScalar<Integer> top, ObservableScalar<Integer> right,
                   ObservableScalar<Integer> bottom, ObservableScalar<Integer> left, ObservableScalar<Node> content) {
        this.content.bindTo(content);
        this.top.bindTo(top);
        this.right.bindTo(right);
        this.bottom.bindTo(bottom);
        this.left.bindTo(left);
    }

    // TODO töröljük ki a 4 property-t, és csak egy insets maradjon
    public void bindToInsets(ObservableScalar<Insets> o) {
        top.bindTo(o.map(Insets::top));
        right.bindTo(o.map(Insets::right));
        bottom.bindTo(o.map(Insets::bottom));
        left.bindTo(o.map(Insets::left));
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        ObservableScalar<Size> all = binding(left, right, top, bottom, (l, r, t, b) -> new Size(l + r, t + b));
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
