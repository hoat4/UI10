package ui10.layout;

import ui10.geom.Size;
import ui10.pane.Pane;

import static ui10.geom.Point.ORIGO;

public class StackPane extends AbstractLayoutPane {

    public StackPane() {
    }

    public StackPane(Pane... children) {
        super(children);
    }

    @Override
    public AbstractLayout computeLayout(BoxConstraints constraints) {
        Size size = children().stream().map(n -> n.layout(constraints).size()).reduce(Size::max).orElse(constraints.min());
        return new AbstractLayout(constraints, size) {
            @Override
            public void apply() {
                children().forEach(n -> applyChild(n.layout(BoxConstraints.fixed(size)), ORIGO));
            }
        };
    }
}
