package ui10.input;

import ui10.binding.ObservableScalar;
import ui10.pane.Pane;
import ui10.pane.WrapperPane;

public class EventTargetPane extends WrapperPane {

    public final InputEventHandler eventHandler;

    public EventTargetPane(InputEventHandler eventHandler, Pane pane) {
        super(pane);
        this.eventHandler = eventHandler;
    }

    public EventTargetPane(InputEventHandler eventHandler, ObservableScalar<? extends Pane> content) {
        super(content);
        this.eventHandler = eventHandler;
    }
}
