/*
package ui10.controls;

import ui10.binding.ScalarProperty;
import ui10.decoration.Tag;

import static ui10.decoration.Tag.tag;

public class TextButton extends Button<Label> {

    public static final Tag LABEL_TAG = new Tag("TextButtonLabel");

    public final ScalarProperty<String> text = ScalarProperty.create();

    public TextButton() {
        this(null);
    }

    public TextButton(String text) {
        this.text.set(text);
        content.set(tag(new Label(this.text), LABEL_TAG));
    }

}
*/