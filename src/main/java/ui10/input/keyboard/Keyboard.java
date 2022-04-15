package ui10.input.keyboard;

import ui10.binding.EventBus;
import ui10.binding.ObservableList;

import java.util.Optional;
import java.util.Set;

public interface Keyboard {

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

    interface StandardKey {
    }

    record StandardTextKey(String text) implements StandardKey {
    }

    enum StandardFunctionKey implements StandardKey {}

    interface Symbol {

        Optional<StandardSymbol> standardSymbol();

        @Override
        String toString();
    }

    interface StandardSymbol {
        @Override
        String toString();
    }

    record StandardTextSymbol(String text) implements StandardSymbol {}

    enum StandardFunctionSymbol implements StandardSymbol {
        LEFT, RIGHT, UP, DOWN, BACKSPACE, DELETE
    }

    interface Modifier {}

    enum StandardModifier {}
}
