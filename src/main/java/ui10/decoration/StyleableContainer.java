package ui10.decoration;

import ui10.base.*;

public abstract class StyleableContainer<D extends Style> extends LightweightContainer {

    private D decoration;

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
        Element content = contentImpl();
        if (content == null)
            throw new NullPointerException(getClass().getName()+"::contentImpl returned null: "+this);
        return decorate(content);
    }

    protected Element decorate(Element content) {
        return decoration.wrapContent(content);
    }

    protected abstract Element contentImpl();

}
