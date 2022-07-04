package ui10.controls;

import ui10.base.Element;
import ui10.base.ContentEditable;
import ui10.binding7.InvalidationMark;
import ui10.binding9.OVal;

import java.awt.datatransfer.StringSelection;

import static ui10.base.ContentEditable.TraversalDirection.BACKWARD;
import static ui10.base.ContentEditable.TraversalDirection.FORWARD;
import static ui10.base.ContentEditable.TraversalUnit.CHARACTER;

public class InputField<C extends Element & ContentEditable<P>, P extends ContentEditable.ContentPoint>
        extends ui10.base.ElementModel {

    public final C content;
    public final OVal<P> caretPosition = new OVal<>();
    public final OVal<String> placeholder = new OVal<>(); // TODO

    public InputField(C content) {
        this.content = content;
        caretPosition.set(content.leftEnd());
    }

    public void typeText(String text) {
        ContentEditable.ContentRange<P> s = content.insert(caretPosition.get(), new StringSelection(text));
        caretPosition.set(s.end());
    }

    public void caretLeft() {
        content.select(null);
        P p = content.traverse(caretPosition.get(), BACKWARD, CHARACTER);
        if (p != null)
            caretPosition.set(p);
    }

    public void caretRight() {
        content.select(null);
        P p = content.traverse(caretPosition.get(), FORWARD, CHARACTER);
        if (p != null)
            caretPosition.set(p);
    }

    public void backspace() {
        P leftPos = content.traverse(caretPosition.get(), BACKWARD, CHARACTER);
        if (leftPos != null)
            caretPosition.set(content.delete(new ContentEditable.ContentRange<>(leftPos, caretPosition.get())));
    }

    public void delete() {
        P rightPos = content.traverse(caretPosition.get(), FORWARD, CHARACTER);
        if (rightPos != null)
            caretPosition.set(content.delete(new ContentEditable.ContentRange<>(rightPos, caretPosition.get())));
    }
}
