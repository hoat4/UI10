package ui10.base;

import ui10.binding9.Bindings;

public abstract class LightweightContainer extends Element {

    protected abstract Element content();

    @Override
    void initView() {
        Bindings.repeatIfInvalidated(() -> {
            Element content = content();
            next.set(content);
            content.initParent(this);
        });
    }
}
