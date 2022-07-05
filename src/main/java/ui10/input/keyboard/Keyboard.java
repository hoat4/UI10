/*
package ui10.input;

import ui10.binding.ObservableList;

import java.util.Optional;
import java.util.Set;

public interface Keyboard extends EventSource {

    ObservableList<Key> pressedKeys(); // TODO list vagy set legyen?

    Set<KeyboardLed> leds();

    interface PhysicalKeyboard extends Keyboard {
    }

    interface VirtualKeyboard extends Keyboard {
    }

    interface KeyboardHub extends Keyboard {
    }

    interface KeyboardLed {
    }

    enum StandardKeyboardLed implements KeyboardLed {CAPS_LOCK, SCROLL_LOCK, NUM_LOCK}


    interface Key {

        Optional<StandardKey> standardKey();

        @Override
        String toString();
    }

    sealed interface StandardKey {
    }

    record StandardTextKey(String text) implements StandardKey {
    }

    enum StandardFunctionKey implements StandardKey {}

    interface Symbol {

        @Override
        String toString();
    }

    sealed interface StandardSymbol extends Symbol {
    }

    record StandardTextSymbol(String text) implements StandardSymbol {
    }

    enum StandardFunctionSymbol implements StandardSymbol {
        LEFT, RIGHT, UP, DOWN, BACKSPACE, DELETE
    }

    interface Modifier {
    }

    enum StandardModifier {}
}
*/