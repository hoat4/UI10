package ui10.controls;

import ui10.base.ElementModel;

public class Label extends ElementModel<Label.LabelModelListener> {

    private String text;

    public Label() {
    }

    public Label(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }

    public void text(String text) {
        this.text = text;
    }

    public interface LabelModelListener extends ElementModelListener {

        void textChanged();
    }
}
