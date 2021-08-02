package ui10.input;

import java.util.function.Consumer;

public interface InputEventHandler {

    void capture(InputEvent event);

    void bubble(InputEvent event);

    static InputEventHandler of(Consumer<InputEvent> handler) {
        return new InputEventHandler() {
            @Override
            public void capture(InputEvent event) {
            }

            @Override
            public void bubble(InputEvent event) {
                handler.accept(event);
            }
        };
    }

}
