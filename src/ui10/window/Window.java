package ui10.window;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.nodes.Node;
import ui10.nodes.WrapperPane;

public class Window extends WrapperPane {
    public final ScalarProperty<Boolean> shown = ScalarProperty.create();

    public Window() {
    }

    public Window(Node content) {
        super(content);
    }

    public Window(ObservableScalar<Node> content) {
        super(content);
    }
}
