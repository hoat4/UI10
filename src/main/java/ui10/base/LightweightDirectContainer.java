package ui10.base;

import ui10.binding9.OVal;

public class LightweightDirectContainer extends Element {

    public OVal<Element> content() {
        return next;
    }

    @Override
    void initView() {
        next.get().initParent(this);
    }
}
