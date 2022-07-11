package ui10.controls;

import ui10.base.ContentEditable;
import ui10.base.Element;
import ui10.binding9.OVal;
import ui10.font.TextStyle;
import ui10.geom.Point;
import ui10.geom.shape.Shape;
import ui10.graphics.FontWeight;
import ui10.graphics.TextLayout;


public class TextElement extends Element {
/*

    public static final Property<Integer> FONT_SIZE_PROPERTY = new Property<>(true);
    public static final Property<Fill> TEXT_FILL_PROPERTY = new Property<>(true);
    public static final Property<FontWeight> FONT_WEIGHT_PROPERTY = new Property<>(true);
    public static final Property<String> TEXT_PROPERTY = new Property<>(true, "");

 */

    public final OVal<String> text = new OVal<>() {
        @Override
        protected String normalize(String value) {
            return value == null ? "" : value;
        }
    };

    public final OVal<Element> fill = new OVal<>();
    public final OVal<TextStyle> textStyle = new OVal<>();
    public final OVal<FontWeight> fontWeight = new OVal<>(FontWeight.NORMAL); // ennek inkább TextStyle-be kéne tartoznia


    public String text() {
        return text.get();
    }

    public void text(String text) {
        this.text.set(text);
    }

    public final Element fill() {
        return fill.get();
    }

    public final void fill(Element fill) {
        this.fill.set(fill);
    }

    public TextStyle textStyle() {
        return textStyle.get();
    }

    public void textStyle(TextStyle textStyle) {
        this.textStyle.set(textStyle);
    }

    public TextLayout textLayout() {
        return ((TextView) view()).textLayout();
    }

    public interface TextView {

        TextLayout textLayout();
    }

    @Override
    public String toString() {
        return "TextElement{" +
                "text='" + text.get() + '\'' +
                '}';
    }
}
