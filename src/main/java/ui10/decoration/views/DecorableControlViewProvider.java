package ui10.decoration.views;

import ui10.base.*;
import ui10.controls.*;
import ui10.controls.dialog.Dialog;
import ui10.controls.dialog.DialogView;

public class DecorableControlViewProvider implements ViewProvider {

    @Override
    public ViewProviderResult makeView(Element e) {
        e = makeViewImpl(e);
        return e == null ? NoViewResult.UNKNOWN_ELEMENT : new ViewResult(e);
    }

    private Element makeViewImpl(Element e) {
        if (e instanceof TextView m)
            return new StyleableLabelView(m);
        if (e instanceof InputField m)
            return new StyleableTextFieldView(m);
        if (e instanceof Button m)
            return new StyleableButtonView(m);
        if (e instanceof TabbedPane m)
            return new StyleableTabbedPaneView(m);
        if (e instanceof Dialog m)
            return new DialogView(m);
        if (e instanceof Dialog.DialogButton m)
            return new DialogButtonView(m);
        if (e instanceof Table t)
            return new TableView<>(t);

        if (e instanceof Container m && !(e instanceof StyleableView))
            return new StyleableContainerView(m);
        return null;
    }
}
