package ui10.controls;

import ui10.binding7.InvalidationMark;

public class SmallStateControl<S> extends ui10.base.ElementModel {

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

    public enum SmallStateControlProperty implements InvalidationMark {

        STATE
    }
}
