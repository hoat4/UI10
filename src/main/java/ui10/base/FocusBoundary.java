package ui10.base;

import ui10.controls.Button;
import ui10.input.EventInterpretation;
import ui10.input.EventResultWrapper;
import ui10.input.keyboard.KeyCombination;
import ui10.input.keyboard.KeySymbol;

public class FocusBoundary extends LightweightDirectContainer {

    public FocusBoundary() {
    }

    public FocusBoundary(Element content) {
        content().set(content);
    }

    @EventHandler
    EventInterpretation.OKResult handleKeyEvent(EventInterpretation.KeyCombinationEvent keyCombinationEvent) {
        if (keyCombinationEvent.keyCombination().equals(ENTER)) {
            EventResultWrapper<EventInterpretation.ReleaseCallback> rcw =
                    dispatchEvent(new EventInterpretation.BeginButtonRolePress(Button.Role.DEFAULT));

            if (rcw != null) {
                rcw.response().commit();
                return new EventInterpretation.OKResult();
            }
        }
        return null;
    }

    private static final KeyCombination ENTER = new KeyCombination(KeySymbol.StandardFunctionSymbol.ENTER);
}
