package ui10.nodes2;

import ui10.binding.ObservableList;
import ui10.binding.ScalarProperty;
import ui10.font.TextStyle;
import ui10.font.FontMetrics;
import ui10.layout.BoxConstraints;

public class TextPane extends AbstractPane{

    private String text;
    private TextStyle font;

    public TextPane() {
    }

    public TextPane(TextStyle font ) {
        this.font = font;
    }

    public TextPane(TextStyle font, String text) {
        this.font = font;
        this.text = text;
    }

    public ScalarProperty<String> text() {
        return property((TextPane n) -> n.text, (n, v) -> n.text = v);
    }

    public ScalarProperty<TextStyle> textStyle() {
        return property((TextPane n) -> n.font, (n, v) -> n.font = v);
    }

    @Override
    protected ObservableList<? extends FrameImpl> makeChildList() {
        return null;
    }

    @Override
    public AbstractLayout computeLayout(BoxConstraints constraints) {
        FontMetrics m = textStyle().get().textSize(text().get());
        return new AbstractLayout(constraints, m.size()) {
            @Override
            public void apply() {
            }
        };
    }
}
