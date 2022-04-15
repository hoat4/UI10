package ui10.input;

import java.util.function.Consumer;

public interface InputEventHandler {

    boolean capture(InputEvent event);

    boolean bubble(InputEvent event);

    static InputEventHandler of(Consumer<InputEvent> handler) {
        return new InputEventHandler() {
            @Override
            public boolean capture(InputEvent event) {
                return false;
            }

            @Override
            public boolean bubble(InputEvent event) {
                handler.accept(event);
                return false;
            }
        };
    }

}
