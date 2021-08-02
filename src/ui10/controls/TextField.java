package ui10.controls;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.decoration.Tag;
import ui10.input.EventTargetPane;
import ui10.input.InputEnvironment;
import ui10.input.InputEvent;
import ui10.input.InputEventHandler;
import ui10.input.keyboard.KeyTypeEvent;
import ui10.input.pointer.MouseTarget;
import ui10.pane.Pane;

import static ui10.decoration.Tag.tag;

public class TextField extends Control {

    public static final Tag TAG = new Tag("TextField");
    public static final Tag LABEL_TAG = new Tag("TextFieldLabel");

    private String text;
    private boolean focused;

    public ScalarProperty<String> text() {
        return property((TextField t) -> t.text, (t, v) -> t.text = v);
    }

    {
        tag(this, TAG);
    }

    @Override
    protected Pane makeContent() {
        return tag(new Label(text()), LABEL_TAG);
    }

    private ScalarProperty<Boolean> focusedProp() {
        return property((TextField f) -> f.focused, (f, v) -> f.focused = v);
    }

    public ObservableScalar<Boolean> focused() {
        return focusedProp();
    }

    @Override
    protected Pane wrapDecoratedContent(Pane decoratedContent) {
        EventTargetPane e = new EventTargetPane(InputEventHandler.of(this::handleEvent), decoratedContent);
        MouseTarget m = new MouseTarget(e);
        m.pressedButtons.subscribe(l -> inputEnvironment().get().focus().set(e));
        focusedProp().bindTo(inputEnvironment().flatMap(InputEnvironment::focus), f -> f == e);
        return m;
    }

    private void handleEvent(InputEvent event) {
        if (event instanceof KeyTypeEvent e) {
            text().set(text + e.text());
        }
    }
}
