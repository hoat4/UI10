package ui10.nodes2;

import ui10.binding.ScalarProperty;
import ui10.layout.BoxConstraints;

public class Window extends AbstractSingleNodeLayoutPane {

    private boolean shown;

    public Window(Pane content) {
        super(content);
    }

    public ScalarProperty<Boolean> shown() {
        return property((Window w) -> w.shown, (w, v) -> w.shown = v);
    }

    @Override
    public AbstractLayout computeLayout(BoxConstraints constraints) {
        return AbstractLayout.wrap(currentContentFrame().layout(constraints));
    }
}
