package ui10.decoration.views;

import ui10.base.Element;
import ui10.base.TextAlign;
import ui10.controls.Label;
import ui10.controls.TextElement;
import ui10.decoration.Style;
import ui10.decoration.Fill;
import ui10.font.TextStyle;
import ui10.layout.Layouts;

// TODO text-align legyen állítható CSS-ből
// .label
public class StyleableLabelView extends StyleableView<Label, StyleableLabelView.LabelStyle>
        implements Label.LabelModelListener {

    private final TextElement textElement = new TextElement(); // .label-text

    public StyleableLabelView(Label model) {
        super(model);
    }

    @Override
    protected Element contentImpl() {
        return switch (decoration().textAlign()) {
            case LEFT -> textElement;
            case CENTER -> Layouts.halign(Layouts.HorizontalAlignment.CENTER, textElement);
            case RIGHT -> Layouts.halign(Layouts.HorizontalAlignment.RIGHT, textElement);
        };
    }

    @Override
    @Setup
    public void textChanged() {
        textElement.text(model.text());
    }

    @Setup
    public void textColorChanged() {
        textElement.fill(decoration().textFill().makeElement(null));
    }

    @Setup
    public void textStyleChanged() {
        textElement.textStyle(decoration().textStyle());
    }

    public void textAlignChanged() {
        invalidate();
    }

    public interface LabelStyle extends Style {

        TextAlign textAlign();

        Fill textFill();

        TextStyle textStyle();
    }
}
