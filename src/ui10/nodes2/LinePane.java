package ui10.nodes2;

import ui10.binding.ObservableList;
import ui10.layout.BoxConstraints;
import ui10.pane.AbstractPane;
import ui10.pane.FrameImpl;

public class LinePane extends AbstractPane {

    @Override
    protected ObservableList<? extends FrameImpl> makeChildList() {
        return null;
    }

    @Override
    public AbstractLayout computeLayout(BoxConstraints constraints) {
        return new AbstractLayout(constraints, constraints.min()) {
            @Override
            public void apply() {
            }
        };
    }

}
