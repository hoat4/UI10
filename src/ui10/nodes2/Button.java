package ui10.nodes2;

import ui10.binding.ScalarProperty;
import ui10.decoration.Tag;

import static ui10.decoration.Tag.tag;

public class Button<P extends Pane> extends Control {

    public static final Tag TAG = new Tag("Button");

    private P content;

    public Button() {
    }

    public Button(P content) {
        this.content = content;
    }

    public ScalarProperty<P> content() {
        return property((Button<P> b) -> b.content, (b, v) -> b.content = v);
    }

    @Override
    protected Pane makeContent() {
        return tag(new WrapperPane(content()), TAG);
    }
}
