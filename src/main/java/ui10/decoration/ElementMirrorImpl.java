package ui10.decoration;

import ui10.base.Element;
import ui10.controls.Button;
import ui10.decoration.css.ElementMirror;
import ui10.decoration.views.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ElementMirrorImpl implements ElementMirror {

    final StyleableContainer<?> element;

    public final Set<Object> interests = new HashSet<>();

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
                    interests.add(Button.ButtonProperty.PRESSED);
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
}
