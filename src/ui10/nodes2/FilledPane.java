package ui10.nodes2;

import ui10.binding.ObservableList;
import ui10.binding.ScalarProperty;
import ui10.image.Color;
import ui10.layout.BoxConstraints;

public class FilledPane extends AbstractPane{

    private Color color;

    public FilledPane() {
    }

    public FilledPane(Color color) {
        this.color = color;
    }

    public ScalarProperty<Color> color() {
        return property((FilledPane n) -> n.color, (n, v) -> n.color = v);
    }

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
