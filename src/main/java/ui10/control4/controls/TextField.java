package ui10.control4.controls;

import ui10.base.ElementModel;

public class TextField extends ElementModel<TextField.TextFieldListener> {

    private String text = "";
    private int caretPosition;
    private Selection selection;

    public String text() {
        return text;
    }

    public void text(String text) {
        if (text == null)
            text = "";
        this.text = text;
        listener().textChanged();
    }

    public String placeholder() {
        return null;
    }

    public int caretPosition() {
        return caretPosition;
    }

    public void caretPosition(int caretPosition) {
        this.caretPosition = caretPosition;
        listener().caretPositionChanged();
    }

    public Selection selection() {
        return selection;
    }

    public void selection(Selection selection) {
        this.selection = selection;
        listener().selectionChanged();
    }

    public record Selection(int begin, int end) {
    }

    public interface TextFieldListener extends ElementModelListener {

        default void textChanged() {
        }

        default void placeholderChanged() {
        }

        default void caretPositionChanged() {
        }

        default void selectionChanged() {
        }
    }
}
