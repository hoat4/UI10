package ui10.base;

import ui10.binding9.Bindings;
import ui10.binding9.OVal;

public abstract class Container extends Element {

    public final OVal<Element> contentProp = new OVal<>();

    @RepeatedInit
    void initContent() {
        contentProp.set(content());
    }

    protected abstract Element content();

    @Override
    void initView() {
        super.initView();
        if (next.get() == null) { // TODO itt miért van ez a feltétel?
            Bindings.repeatIfInvalidated(() -> {
                Element content = contentProp.get();
                next.set(content);
                content.initParent(this);
            });
        }
    }
}
