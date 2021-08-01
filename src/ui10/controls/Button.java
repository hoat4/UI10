package ui10.controls;

import ui10.binding.EventBus;
import ui10.binding.ObservableList;
import ui10.binding.ScalarProperty;
import ui10.binding.StandaloneEventBus;
import ui10.decoration.Tag;
import ui10.input.MouseEvent;
import ui10.input.MouseTarget;
import ui10.pane.Pane;
import ui10.pane.WrapperPane;

import static ui10.decoration.Tag.tag;

public class Button<P extends Pane> extends Control {

    public static final Tag TAG = new Tag("Button");

    private P content;
    private final ScalarProperty<Boolean> pressed = ScalarProperty.<Boolean>create().set(false);
    private final EventBus<Void> onClick = new StandaloneEventBus<>();

    {
        tag(this, TAG);
    }

    public Button() {
    }

    public Button(P content) {
        this.content = content;
    }

    public ScalarProperty<P> content() {
        return property((Button<P> b) -> b.content, (b, v) -> b.content = v);
    }

    public ScalarProperty<Boolean> pressed() {
        return pressed;
    }

    @Override
    protected Pane makeContent() {
        return new WrapperPane(content());
    }

    @Override
    protected Pane wrapDecoratedContent(Pane decoratedContent) {
        MouseTarget mouseTarget = new MouseTarget(decoratedContent);
        mouseTarget.pressedButtons.subscribe(ObservableList.simpleListSubscriber(this::mousePressed, this::mouseReleased));
        return mouseTarget;
    }

    public EventBus<Void> onClick() {
        return onClick;
    }

    private void mousePressed(MouseEvent.MouseButton button) {
        if (button != MouseEvent.MouseButton.LEFT_BUTTON)
            return;

        pressed.set(true);
    }

    private void mouseReleased(MouseEvent.MouseButton button) {
        if (button != MouseEvent.MouseButton.LEFT_BUTTON)
            return;

        pressed.set(false);
        onClick.postEvent(null);
    }

}
