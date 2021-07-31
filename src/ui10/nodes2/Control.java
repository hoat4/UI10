package ui10.nodes2;

import ui10.binding.ObservableList;
import ui10.layout.BoxConstraints;

import static ui10.geom.Point.ORIGO;

public abstract class Control extends AbstractPane{

    protected abstract Pane makeContent();

    @Override
    protected ObservableList<? extends FrameImpl> makeChildList() {
        return ObservableList.ofConstantElement(new FrameImpl(makeContent()));
    }

    @Override
    public AbstractLayout computeLayout(BoxConstraints constraints) {
        Frame.FrameAndLayout contentLayout = children().get(0).layout(constraints);
        return new AbstractLayout(constraints, contentLayout.size()) {
            @Override
            public void apply() {
                applyChild(contentLayout, ORIGO);
            }
        };
    }
}
