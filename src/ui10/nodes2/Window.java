package ui10.nodes2;

import ui10.binding.ScalarProperty;
import ui10.layout.AbstractSingleNodeLayoutPane;
import ui10.layout.BoxConstraints;
import ui10.pane.Pane;

public class Window extends AbstractSingleNodeLayoutPane {

    private boolean shown;

    public Window(Pane content) {
        super(content);
    }

    // public ObservableScalar<Boolean> shown() { return parent().map(Objects::nonNull); }

    public ScalarProperty<Boolean> shown() {
        return property((Window w) -> w.shown, (w, v) -> w.shown = v);
    }

    @Override
    public AbstractLayout computeLayout(BoxConstraints constraints) {
        return AbstractLayout.wrap(currentContentFrame().layout(constraints));
    }
}
