/*
package ui10.controls;

import ui10.base.*;
import ui10.binding.Observable;
import ui10.binding2.ActionEvent;
import ui10.binding2.ChangeEvent;
import ui10.binding2.ElementEvent;
import ui10.binding2.Property;
import ui10.decoration.css.CSSClass;
import ui10.graphics.TextNode;
import ui10.input.pointer.MouseEvent;
import ui10.layout.Layouts;

import java.util.Set;

import static ui10.decoration.css.CSSClass.withClass;

public class Button extends ControlModel {

    public static final Property<String> TEXT_PROPERTY = new Property<>(false, "");
    public static final Property<Boolean> PRESSED_PROPERTY = new Property<>(false, false);
    public static final Property<Boolean> ACTION_EVENT = new Property<>(false);
    public static final Property<Boolean> DEFAULT_PROPERTY = new Property<>(false);

    public Button() {
        this("");
    }

    public Button(String text) {
        text(text);
        view = new ButtonView(this);
    }

    public boolean pressed() {
        return getProperty(PRESSED_PROPERTY);
    }

    public void pressed(boolean pressed) {
        setProperty(PRESSED_PROPERTY, pressed);
    }

    public boolean defaultButton() {
        return getProperty(DEFAULT_PROPERTY);
    }

    public void defaultButton(boolean defaultButton) {
        setProperty(DEFAULT_PROPERTY, defaultButton);
    }

    public String text() {
        return getProperty(TEXT_PROPERTY);
    }

    public void text(String text) {
        setProperty(TEXT_PROPERTY, text);
    }

    public Observable<ActionEvent> onAction() {
        return (Observable<ActionEvent>) observable(ACTION_EVENT);
    }

    @Override
    public <T> void setProperty(Property<T> prop, T value) {
        if (prop.equals(DEFAULT_PROPERTY)) {
            if ((boolean) value)
                setProperty(new CSSClass("default-button"), null);
            else
                removeProperty(new CSSClass("default-button"));
        }
        super.setProperty(prop, value);
    }

    @Override
    public String elementName() {
        return "Button";
    }

    private static class ButtonView extends ControlView<Button> {

        private final TextNode textNode = withClass("button-text", new TextNode());

        public ButtonView(Button button) {
            super(button);
        }

        @Override
        public String elementName() {
            return "ButtonView";
        }

        @Override
        protected Element content() {
            return Layouts.centered(textNode); // ezt CSS-ből kéne
        }

        /*
        @Watch("model.text")
        void onTextChange() {
            textNode.text(model.text());
        }

        @Watch("model.pressed")
        void onPressChange() {
            if (!model.pressed())
                model.dispatchElementEvent(new ActionEvent(Button.ACTION_EVENT));
        }
 */
/*
        @Override
        protected void initFromProps() {
            super.initFromProps();
            textNode.text(model.text());
        }

        @Override
        protected Set<Property<?>> modelPropertySubscriptions() {
            return Set.of(PRESSED_PROPERTY);
        }

        @Override
        protected void handleModelEvent(ElementEvent evt) {
            if (evt instanceof ChangeEvent ce && ce.property().equals(Button.PRESSED_PROPERTY)) {
                System.out.println(model.pressed());

                if (!model.pressed())
                    model.dispatchElementEvent(new ActionEvent(Button.ACTION_EVENT));
            }
        }

        @EventHandler
        private void onMousePress(MouseEvent.MousePressEvent event, EventContext eventContext) {
            focusContext().focusedControl.set(this);
            model.pressed(true);
        }

        @EventHandler
        private void onMouseRelease(MouseEvent.MouseReleaseEvent event, EventContext eventContext) {
            model.pressed(false);
        }

    }
}
*/