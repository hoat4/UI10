package ui10.decoration;

import ui10.base.ElementExtra;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.views.*;

public class StyleProvider extends ElementExtra {

    private final CSSDecorator css;

    public StyleProvider(CSSDecorator css) {
        this.css = css;
    }

    public <D extends Style> D makeDecoration(StyleableContainer<D> view) {
        if (view instanceof StyleableButtonView v)
            return (D) new ButtonCSS(v, css);
        if (view instanceof StyleableTextFieldView v)
            return (D) new TextFieldCSS(v, css);
        if (view instanceof StyleableLabelView v)
            return (D) new LabelCSS(v, css);
        if (view instanceof StyleableContainerView v)
            return (D) new CSSStyle<>(v, css);
        if (view instanceof StyleableTabbedPaneView v)
            return (D) new CSSStyle<>(v, css);
        if (view instanceof StyleableTabbedPaneView.TabButton v)
            return (D) new CSSStyle<>(v, css);
        if (view instanceof StyleableTabbedPaneView.TabHeaderArea v)
            return (D) new CSSStyle<>(v, css);
        if (view instanceof StyleableTabbedPaneView.TabButtonInner v)
            return (D) new CSSStyle<>(v, css);
        if (view instanceof StyleableTabbedPaneView.TabButtons v)
            return (D) new CSSStyle<>(v, css);
        if (view instanceof StyleableTabbedPaneView.TabHeaderBackground v)
            return (D) new CSSStyle<>(v, css);
        throw new UnsupportedOperationException("unknown view: " + view);
    }
}
