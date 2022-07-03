package ui10.controls;

import ui10.base.ElementModel;
import ui10.base.Element;
import ui10.binding7.InvalidationListener;
import ui10.binding7.InvalidationMark;
import ui10.font.TextStyle;
import ui10.graphics.TextLayout;


public class TextElement extends ElementModel {
/*

    public static final Property<Integer> FONT_SIZE_PROPERTY = new Property<>(true);
    public static final Property<Fill> TEXT_FILL_PROPERTY = new Property<>(true);
    public static final Property<FontWeight> FONT_WEIGHT_PROPERTY = new Property<>(true);
    public static final Property<String> TEXT_PROPERTY = new Property<>(true, "");

 */

    private String text = "";
    private Element fill;
    private TextStyle textStyle;

    public String text() {
        return text;
    }

    public void text(String text) {
        if (text == null)
            text = "";
        this.text = text;
        invalidate(TextElementProperty.TEXT);
    }

    public final Element fill() {
        return fill;
    }

    public final void fill(Element fill) {
        this.fill = fill;
        invalidate(TextElementProperty.FILL);
    }

    public TextStyle textStyle() {
        return textStyle;
    }

    public void textStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
        // listener().textStyleChanged();
    }

    public TextLayout textLayout() {
        return ((TextView) view()).textLayout();
    }

    public interface TextView extends InvalidationListener {

        TextLayout textLayout();
    }

    @Override
    public String toString() {
        return "TextElement{" +
                "text='" + text + '\'' +
                '}';
    }

    public enum TextElementProperty implements InvalidationMark {
        TEXT, FILL
    }
}
