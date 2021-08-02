package ui10.input.keyboard;

import ui10.input.InputEvent;

public interface KeyTypeEvent extends InputEvent {

    // Set<Keyboard> keyboard();

    // Set<Keyboard.Key> key(); // vagy legyen list?

    // ClipboardContent content(); // ?

    String text();
}
