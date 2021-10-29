package ui10.ui6;

import ui10.input.InputEvent;

public interface EventHandler {

    boolean capture(InputEvent event);

    boolean bubble(InputEvent event);
}
