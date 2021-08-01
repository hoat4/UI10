package ui10.nodes2;

import ui10.binding.ObservableList;
import ui10.layout.AbstractLayoutPane;
import ui10.layout.BoxConstraints;

public class Desktop extends AbstractLayoutPane {

    public ObservableList<Window> windows() {
        return (ObservableList<Window>) (ObservableList<?>) children;
    }

    @Override
    public AbstractLayout computeLayout(BoxConstraints constraints) {
        return null;
    }
}
