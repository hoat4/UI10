package ui10.nodes;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.font.TextStyle;
import ui10.geom.Size;
import ui10.image.Color;
import ui10.image.Colors;
import ui10.layout.BoxConstraints;

import static ui10.binding.ObservableScalar.binding;

public class TextPane extends Pane {

    public final ScalarProperty<String> text = ScalarProperty.create();
    public final ScalarProperty<TextStyle> textStyle = ScalarProperty.create();
    public final ScalarProperty<Color> textColor = ScalarProperty.createWithDefault(Colors.BLACK);

    public TextPane() {
    }

    public TextPane(TextStyle font) {
        this.textStyle.set(font);
    }

    public TextPane(TextStyle font, String text) {
        this.textStyle.set(font);
        this.text.set(text);
    }

    public TextPane(ObservableScalar<String> text) {
        this.text.bindTo(text);
    }

    public TextPane(ObservableScalar<TextStyle> font, ObservableScalar<String> text) {
        this.textStyle.bindTo(font);
        this.text.bindTo(text);
    }

    @Override
    protected ObservableScalar<Node> paneContent() {
        return ObservableScalar.ofConstant(new PrimitiveNode(this) {

            {
                sizeDependsOn(text);
                sizeDependsOn(textStyle);
            }

            @Override
            public Size determineSize(BoxConstraints constraints) {
                return textStyle.get().textSize(text.get()).size();
            }
        });
    }
}
