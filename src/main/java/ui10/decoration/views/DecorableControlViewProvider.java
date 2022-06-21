package ui10.decoration.views;

import ui10.base.*;
import ui10.controls.Button;
import ui10.controls.Label;
import ui10.controls.TabbedPane;
import ui10.controls.TextField;

public class DecorableControlViewProvider implements ViewProvider {

    @Override
    public Element makeView(ElementModel<?> e) {
        if (e instanceof Label m)
            return new StyleableLabelView(m);
        if (e instanceof TextField m)
            return new StyleableTextFieldView(m);
        if (e instanceof Button m)
            return new StyleableButtonView(m);
        if (e instanceof TabbedPane m)
            return new StyleableTabbedPaneView(m);
        if (e instanceof Container m && !(e instanceof StyleableView))
            return new StyleableContainerView(m);
        return null;
    }
}
