package ui10.base;

public abstract class LightweightFixedContainer extends Element {

    public LightweightFixedContainer(Element content) {
        next.set(content);
    }

    @Override
    void initView() {
        // ezzel az a baj hogy teleszemeteli a bindinges observert
        next.get().initParent(this);
    }

    public final Element content() {
        return next.get();
    }
}
