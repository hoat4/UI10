package ui10.controls.dialog;

import ui10.base.Element;
import ui10.binding2.ElementEvent;
import ui10.binding3.Model;
import ui10.binding3.PropertyIdentifier;
import ui10.input.keyboard.KeyCombination;

import java.util.function.Consumer;

public interface Action extends Model {

    String text();

    default boolean enabled() {
        return true;
    }

    default String description() { // FX-ben "longText"
        return null;
    }

    default Element icon() { // FX-ben "graphic"
        return null;
    }

    default KeyCombination accelerator() {
        return null;
    }
}
