package ui10.decoration;

import ui10.base.Element;
import ui10.controls.Button;
import ui10.decoration.css.ElementMirror;
import ui10.decoration.views.StyleableButtonView;
import ui10.decoration.views.StyleableTabbedPaneView;
import ui10.decoration.views.StyleableTextFieldView;
import ui10.decoration.views.StyleableLabelView;

import java.util.Optional;

public class ElementMirrorImpl implements ElementMirror {

    final StyleableContainer<?> element;

    private boolean updateOnButtonPressedChanged;

    public ElementMirrorImpl(StyleableContainer<?> element) {
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

        return element.getClass().getSimpleName();
    }

    @Override
    public boolean hasClass(String className) {
        return switch (className) {
            default -> false;
        };
    }

    @Override
    public boolean hasPseudoClass(String pseudoClass) {
        return switch (pseudoClass) {
            case "root" -> parent() == null;
            case "active" -> {
                if (element instanceof StyleableButtonView btn) {
                    updateOnButtonPressedChanged = true;
                    yield btn.model.pressed();
                } else if (element instanceof StyleableTabbedPaneView.TabButton btn)
                    yield btn.isSelected();
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
        while (e != null && !(e instanceof StyleableContainer<?>))
            e = e.parent();
        return e == null ? null : new ElementMirrorImpl((StyleableContainer<?>) e);
    }

    // IDE bug
    @SuppressWarnings("RedundantCast")
    public void installListeners() {
        if (updateOnButtonPressedChanged) {
            ((StyleableButtonView) element).model.listeners().add(new Button.ButtonModelListener() {
                @Override
                public void pressedChanged() {
                    element.invalidateDecoration();
                }
            });
        }
    }
}
