package ui10.base;

public abstract class LightweightFixedContainer extends Element {

    public LightweightFixedContainer(Element content) {
        next = content;
    }

    @Override
    void initView() {
        next.initParent(this);
    }

    public final Element content() {
        return next;
    }
}
