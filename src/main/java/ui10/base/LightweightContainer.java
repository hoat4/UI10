package ui10.base;

public abstract class LightweightContainer extends Element {

    protected abstract Element content();

    @Override
    void initView() {
        next = content();
        next.initParent(this);
    }
}
