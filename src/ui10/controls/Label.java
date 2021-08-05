package ui10.controls;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.font.TextStyle;
import ui10.nodes.Node;
import ui10.nodes.Pane;
import ui10.nodes.TextPane;

public class Label extends Pane {

    // TODO legyen külön TextPane és Label is?
    //      https://stackoverflow.com/questions/24374867/label-and-text-differences-in-javafx

    public final ScalarProperty<String> text = ScalarProperty.create();
    public final ScalarProperty<TextStyle> textStyle = ScalarProperty.create();

    public Label() {
    }

    public Label(ObservableScalar<String> text) {
        this.text.bindTo(text);
    }

    public Label(String text) {
        this.text.set(text);
    }

    @Override
    protected ObservableScalar<Node> paneContent() {
        return ObservableScalar.ofConstant(new TextPane(textStyle, text));
    }
}
