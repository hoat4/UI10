package ui10.pane;

import ui10.binding.ObservableScalar;
import ui10.layout.AbstractSingleNodeLayoutPane;
import ui10.layout.BoxConstraints;

public class WrapperPane extends AbstractSingleNodeLayoutPane {

    // TODO legyen default konstruktor is?
    //      a Pane-es mit csinál(jon), ha nullt kap?

    public WrapperPane(Pane content) {
        super(content);
    }

    public WrapperPane(ObservableScalar<? extends Pane> content) {
        super(content);
    }

    @Override
    public AbstractLayout computeLayout(BoxConstraints constraints) {
        return AbstractLayout.wrap(currentContentFrame().layout(constraints));
    }
}
