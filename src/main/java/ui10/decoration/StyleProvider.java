package ui10.decoration;

import ui10.base.ElementExtra;
import ui10.controls.dialog.DialogView;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.views.TabbedPaneCSS;
import ui10.decoration.views.*;

public class StyleProvider extends ElementExtra {

    private final CSSDecorator css;

    public StyleProvider(CSSDecorator css) {
        this.css = css;
    }

    @SuppressWarnings("unchecked")
    public <D extends Style> D makeDecoration(StyleableContainer<D> view) {
        if (view instanceof StyleableButtonView v)
            return (D) new ButtonCSS(v, css);
        if (view instanceof StyleableTextFieldView v)
            return (D) new TextFieldCSS(v, css);
        if (view instanceof StyleableLabelView v)
            return (D) new TextViewCSS(v, css);
        if (view instanceof StyleableTabbedPaneView v)
            return (D) new TabbedPaneCSS(v, css);
        if (view instanceof StyleableTabbedPaneView.TabButton v)
            return (D) new TabButtonCSS(v, css);
        if (view instanceof DialogView v)
            return (D) new DialogCSS(v, css);
        return (D) new CSSStyle<>(view, css);
    }
}
