package ui10.controls;

import ui10.binding.EventBus;
import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.binding.StandaloneEventBus;
import ui10.decoration.Tag;
import ui10.input.EventTarget;
import ui10.input.InputEventHandler;
import ui10.input.pointer.MouseEvent;
import ui10.nodes.Node;
import ui10.nodes.Pane;

public class Button<P extends Pane> extends Pane {

    public static final Tag TAG = new Tag("Button");

    public final ScalarProperty<P> content = ScalarProperty.create();
    private final ScalarProperty<Boolean> pressed = ScalarProperty.<Boolean>create().set(false);
    public final EventBus<Void> onClick = new StandaloneEventBus<>();

    {
        tags().add(TAG);
    }

    public Button() {
    }

    public Button(P content) {
        this.content.set(content);
    }

    public Button(ObservableScalar<? extends P> content) {
        this.content.bindTo(content);
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        return content;
    }

    public ObservableScalar<Boolean> pressed() {
        return pressed;
    }

    @Override
    protected Node wrapDecoratedContent(Node decoratedContent) {
        EventTarget mouseTarget = new EventTarget(decoratedContent);
        mouseTarget.eventHandlers.add(InputEventHandler.of(e -> {
            if (e instanceof MouseEvent.MousePressEvent)
                pressed.set(true);

            if (e instanceof MouseEvent.MouseReleaseEvent) {
                pressed.set(false);
                onClick.postEvent(null);
            }
        }));
        return mouseTarget;
    }
}
