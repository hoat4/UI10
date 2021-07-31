package ui10.nodes2;

import ui10.binding.ObservableScalar;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

public class Centered extends AbstractSingleNodeLayoutPane{

    public Centered() {
    }

    public Centered(Pane content) {
        super(content);
    }

    public Centered(ObservableScalar<? extends Pane> content) {
        super(content);
    }

    @Override
    public AbstractLayout computeLayout(BoxConstraints constraints) {
        Frame.FrameAndLayout contentLayout = currentContentFrame().layout(constraints.withMinimum(Size.ZERO));
        return new AbstractLayout(constraints, contentLayout.size()) {

            @Override
            public void apply() {
                applyChild(contentLayout, Rectangle.of(size).centered(contentLayout.size()).topLeft());
            }
        };
    }
}
