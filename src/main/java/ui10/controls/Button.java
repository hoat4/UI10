package ui10.controls;

import ui10.base.ContentEditable;
import ui10.base.Element;
import ui10.binding9.OVal;
import ui10.geom.Point;
import ui10.geom.shape.Shape;

public class Button extends Element {

    public final Action action;
    public final Element content;

    public final OVal<ButtonState> state = new OVal<>(new ButtonState(false, false, false)) {
        @Override
        protected void afterChange(ButtonState oldValue, ButtonState newValue) {
            if (oldValue.press() && !newValue.press())
                Button.this.action.execute();
        }
    };
    public final OVal<Role> role = new OVal<>(); // ez lehetne sima final változó is

    public Button(Element content, Action action) {
        this.content = content;
        this.action = action;
    }

    public interface Action {

        default boolean isEnabled() {
            return true;
        }

        void execute();
    }

    public record ButtonState(boolean hover, boolean focus, boolean press) {
    }

    public enum Role {
        CANCEL, DEFAULT
    }
}
