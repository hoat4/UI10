package ui10.input.keyboard;

import ui10.input.InputEvent;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TextInputEvent extends InputEvent {

    String text();

    Optional<List<KeyPress>> keyPresses(); // ???

    record KeyPress(Keyboard.Key key, Keyboard.Symbol symbol, Set<Keyboard.Modifier> modifiers) {}
}
