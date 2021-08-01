package ui10.controls;

import ui10.binding.ScalarProperty;
import ui10.decoration.Tag;

import static ui10.decoration.Tag.tag;

public class TextButton extends Button<Label> {

    public static final Tag LABEL_TAG = new Tag("TextButtonLabel");

    private String text;

    public TextButton() {
        this(null);
    }

    public TextButton(String text) {
        this.text = text;
        content().set(tag(new Label(text()), LABEL_TAG));
    }

    public ScalarProperty<String> text() {
        return property((TextButton n) -> n.text, (n, v) -> n.text = v);
    }
}
