package ui10.input.keyboard;

import ui10.input.InputEvent;

import java.util.Optional;

public interface KeyTypeEvent extends InputEvent {

    // Set<Keyboard> keyboard();

    // Set<Keyboard.Key> key(); // vagy legyen list?

    // ClipboardContent content(); // ?

    Keyboard.Symbol symbol();

    // Keyboard.Key key();

}
