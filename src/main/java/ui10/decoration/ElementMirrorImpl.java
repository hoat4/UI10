package ui10.decoration;

import ui10.base.Element;
import ui10.controls.Button;
import ui10.controls.dialog.DialogView;
import ui10.decoration.css.ElementMirror;
import ui10.decoration.views.*;

import java.util.Optional;

public class ElementMirrorImpl implements ElementMirror {

    final StyleableContainer element;

    public ElementMirrorImpl(StyleableContainer element) {
        this.element = element;
    }

    @Override
    public String elementName() {
        if (element instanceof StyleableButtonView)
            return "Button";
        if (element instanceof StyleableLabelView)
            return "Label";
        if (element instanceof StyleableTextFieldView)
            return "TextField";

        ElementName ann = element.getClass().getAnnotation(ElementName.class);
        if (ann != null)
            return ann.value();

        return element.getClass().getSimpleName();
    }

    @Override
    public boolean hasClass(String className) {
        return switch (className) {
            case "dialog-header" -> element instanceof DialogView.DialogHeader;
            case "dialog-main" -> element instanceof DialogView.DialogMain;
            case "dialog-button-bar" -> element instanceof DialogView.DialogButtonBar;
            case "default-button" -> element instanceof StyleableButtonView btn && btn.model.role.get() == Button.Role.DEFAULT;
            default -> {
                if (element instanceof StyleableView<?> view) {
                    ClassName ann = view.model.getClass().getAnnotation(ClassName.class);
                    yield ann != null && className.equals(ann.value());
                }else
                    yield false;
            }
        };
    }

    @Override
    public boolean hasPseudoClass(String pseudoClass) {
        return switch (pseudoClass) {
            case "root" -> parent() == null;
            case "active" -> {
                if (element instanceof StyleableButtonView btn)
                    yield btn.model.state.get().press();
                else if (element instanceof StyleableTabbedPaneView.TabButton btn)
                    yield btn.selected.get();
                else
                    yield false;
            }
            default -> false;
        };
    }

    @Override
    public boolean isPseudoElement(String pseudoElementName) {
        return false;
    }

    @Override
    public Optional<Integer> indexInSiblings() {
        return Optional.empty();
    }

    @Override
    public ElementMirror parent() {
        Element e = element.parent();
        while (e != null && !(e instanceof StyleableContainer))
            e = e.parent();
        return e == null ? null : new ElementMirrorImpl((StyleableContainer) e);
    }
}
