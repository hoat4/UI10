package ui10.input;

import ui10.binding.ScalarProperty;

public interface InputEnvironment {

    ScalarProperty<EventTargetPane> focus();

    // többes fókusz? pl. multiple caret egy szövegszerkesztőben?

    /*
    Keyboard keyboard();

    EventBus<KeyTypeEvent> keyTypes();

    Clipboard clipboard();

    Clipboard selectionClipboard();

    // input method?
    */
}
