package ui10.controls;

import ui10.binding7.PropertyBasedModel;

public class SmallStateControl<S> extends PropertyBasedModel<SmallStateControl.SmallStateControlProperty> {

    private S state;

    protected SmallStateControl(S initialState) {
        this.state = initialState;
    }

    public S state() {
        return state;
    }

    public void state(S state) {
        this.state = state;
        invalidate(SmallStateControlProperty.STATE);
    }

    public enum SmallStateControlProperty {

        STATE
    }
}
