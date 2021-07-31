package ui10.nodes2;

import ui10.binding.ObservableScalar;
import ui10.layout.BoxConstraints;

public class WrapperPane extends AbstractSingleNodeLayoutPane {

    public WrapperPane(ObservableScalar<? extends Pane> content) {
        super(content);
    }

    @Override
    public AbstractLayout computeLayout(BoxConstraints constraints) {
        return AbstractLayout.wrap(currentContentFrame().layout(constraints));
    }
}
