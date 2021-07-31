package ui10.nodes2;

import ui10.binding.ScalarProperty;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

public class AnchorPane extends AbstractItemPane<AnchorPane.AnchoredItem> {

    public AnchorPane() {
    }

    public AnchorPane(AnchoredItem... items) {
        super(items);
    }

    @Override
    public AbstractLayout computeLayout(BoxConstraints constraints) {
        Size size = items().stream().
                map(n -> n.frame().layout(constraints.subtract(n.pos().get())).size()).
                reduce(Size::max).orElse(constraints.min());

        return new AbstractLayout(constraints, size) {

            @Override
            public void apply() {
                for (AnchoredItem item : items()) {
                    item.pos().subscribe(c -> invalidate());
                    Frame.FrameAndLayout childLayout = item.frame().
                            layout(new BoxConstraints(size, size).subtract(item.pos().get()));
                    applyChild(childLayout, item.pos().get());
                }
            }
        };
    }

    public static class AnchoredItem extends Item {

        private final ScalarProperty<Point> point = ScalarProperty.create();

        public AnchoredItem(Point pos, Pane pane) {
            super(pane);
            pos().set(pos);
        }

        public ScalarProperty<Point> pos() {
            return point;
        }
    }
}
