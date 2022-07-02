package ui10.controls;

import ui10.base.Element;
import ui10.base.ContentEditable;
import ui10.binding7.PropertyBasedModel;

import java.awt.datatransfer.StringSelection;

import static ui10.base.ContentEditable.TraversalDirection.BACKWARD;
import static ui10.base.ContentEditable.TraversalDirection.FORWARD;
import static ui10.base.ContentEditable.TraversalUnit.CHARACTER;

public class InputField<C extends Element & ContentEditable<P>, P extends ContentEditable.ContentPoint>
        extends PropertyBasedModel<InputField.InputFieldProperty> {

    public final C content;

    private P caretPosition;

    public InputField(C content) {
        this.content = content;
        caretPosition = content.leftEnd();
    }

    public String placeholder() { // TODO
        return null;
    }

    public P caretPosition() {
        return caretPosition;
    }

    public void caretPosition(P caretPosition) {
        this.caretPosition = caretPosition;
        invalidate(InputFieldProperty.CARET_POSITION);
    }

    public void typeText(String text) {
        ContentEditable.ContentRange<P> s = content.insert(caretPosition, new StringSelection(text));
        caretPosition(s.end());
    }

    public void caretLeft() {
        content.select(null);
        P p = content.traverse(caretPosition, BACKWARD, CHARACTER);
        if (p != null)
            caretPosition(p);
    }

    public void caretRight() {
        content.select(null);
        P p = content.traverse(caretPosition, FORWARD, CHARACTER);
        if (p != null)
            caretPosition(p);
    }

    public void backspace() {
        P leftPos = content.traverse(caretPosition, BACKWARD, CHARACTER);
        if (leftPos != null)
            caretPosition(content.delete(new ContentEditable.ContentRange<>(leftPos, caretPosition)));
    }
    
    public void delete() {
        P rightPos = content.traverse(caretPosition, FORWARD, CHARACTER);
        if (rightPos != null)
            caretPosition(content.delete(new ContentEditable.ContentRange<>(rightPos, caretPosition)));
    }

    public enum InputFieldProperty {
        CARET_POSITION
    }
}
