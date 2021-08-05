package ui10.input;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.nodes.Node;
import ui10.nodes.Pane;

import java.util.ArrayList;
import java.util.List;

public class EventTarget extends Pane { // vagy inkább Node legyen?

    public final ScalarProperty<Node> content = ScalarProperty.create();
    public final List<InputEventHandler> eventHandlers = new ArrayList<>();

    public EventTarget(InputEventHandler handler) {
        eventHandlers.add(handler);
    }

    public EventTarget() {
    }

    public EventTarget(Node content) {
        this.content.set(content);
    }

    public EventTarget(ObservableScalar<? extends Node> content) {
        this.content.bindTo(content);
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        return content;
    }

}
