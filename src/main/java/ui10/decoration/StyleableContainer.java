package ui10.decoration;

import ui10.base.*;

public abstract class StyleableContainer<D extends Style> extends Container {

    private D decoration;

    //private boolean decorationInvalid;

    protected D decoration() {
        return decoration;
    }

    @OnChange(StyleProvider.class)
    private void initDecoration() {
        decoration = lookup(StyleProvider.class).makeDecoration(this);
        if (decoration == null)
            throw new IllegalStateException("null decoration for "+this);
        onDecorationChanged();
    }

    protected void onDecorationChanged() {}
/*
    public void invalidateDecoration() {
        decorationInvalid = true;
        invalidate();
    }
*/
    @Override
    protected Element content() {
        /*
        if (decorationInvalid) {
            initDecoration();
            decorationInvalid = false;
        }
         */
        return decoration.wrapContent(contentImpl());
    }

    protected abstract Element contentImpl();

}
