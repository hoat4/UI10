package ui10.input.keyboard;

import ui10.binding.EventBus;
import ui10.binding.ObservableList;

import java.util.Set;

public interface Keyboard {

    ObservableList<Key> pressedKeys(); // TODO list vagy set legyen?

    Set<KeyboardLed> leds();

    interface Key {
    }

    interface PhysicalKeyboard extends Keyboard {
    }

    interface VirtualKeyboard extends Keyboard {
    }

    interface KeyboardHub extends Keyboard {
    }

    interface KeyboardLed {
    }

    enum StandardKeyboardLed implements KeyboardLed {CAPS_LOCK, SCROLL_LOCK, NUM_LOCK}
}
