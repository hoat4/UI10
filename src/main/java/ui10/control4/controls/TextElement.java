package ui10.control4.controls;

import ui10.base.Element;
import ui10.base.ElementModel;
import ui10.font.FontMetrics;
import ui10.font.TextStyle;
import ui10.graphics.TextLayout;


public abstract class TextElement extends ElementModel<TextElement.TextElementListener> {
/*

    public static final Property<Integer> FONT_SIZE_PROPERTY = new Property<>(true);
    public static final Property<Fill> TEXT_FILL_PROPERTY = new Property<>(true);
    public static final Property<FontWeight> FONT_WEIGHT_PROPERTY = new Property<>(true);
    public static final Property<String> TEXT_PROPERTY = new Property<>(true, "");

 */

    public abstract String text();

    public abstract Element fill();

    public abstract TextStyle textStyle();

    public TextLayout textLayout() {
        return ((TextView) view()).textLayout();
    }

    public interface TextElementListener extends ElementModelListener {

        void textChanged();

        void fillChanged();
    }

    public interface TextView extends TextElementListener {

        TextLayout textLayout();
    }
}
