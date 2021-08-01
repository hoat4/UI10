package ui10.controls;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.font.TextStyle;
import ui10.nodes2.TextPane;
import ui10.pane.Pane;

public class Label extends Control{

    private String text;
    private TextStyle textStyle;

    public Label() {
    }

    public Label(ObservableScalar<String> text) {
        text().bindTo(text);
    }

    public Label(String text) {
        this.text = text;
    }

    public ScalarProperty<String> text() {
        return property((Label l)->l.text, (l, v)->l.text = v);
    }

    public ScalarProperty<TextStyle> textStyle() {
        return property((Label l)->l.textStyle, (l, v)->l.textStyle = v);
    }

    @Override
    protected Pane makeContent() {
        final TextPane textPane = new TextPane();
        textPane.text().bindTo(text());
        textPane.textStyle().bindTo(textStyle());
        return textPane;
    }
}
