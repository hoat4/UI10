package ui10.nodes;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;

public class WrapperPane extends Pane {

    public final ScalarProperty<Node> content = ScalarProperty.create();

    public WrapperPane() {
    }

    public WrapperPane(Node content) {
        this.content.set(content);
    }

    public WrapperPane(ObservableScalar<Node> content) {
        this.content.bindTo(content);
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        return content;
    }
}
