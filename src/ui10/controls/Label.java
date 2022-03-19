package ui10.controls;

import ui10.base.Control;
import ui10.base.Element;
import ui10.decoration.DecorationContext;
import ui10.decoration.css.CSSClass;
import ui10.decoration.css.CSSProperty;
import ui10.decoration.css.Styleable;
import ui10.graphics.TextNode;
import ui10.layout.Layouts;

import java.util.Objects;

public class Label extends Control implements Styleable {

    private static final TextAlign DEFAULT_TEXT_ALIGN = TextAlign.LEFT;

    private TextNode textNode = new TextNode();
    private TextAlign textAlign = DEFAULT_TEXT_ALIGN;

    public Label() {
        this("");
    }

    public Label(String text) {
        textNode.attributes().add(new CSSClass("label-text"));
        textNode.text(text);
        attributes().add(new CSSClass("label"));
    }

    public void text(String text) {
        textNode.text(text);
    }

    public String text() {
        return textNode.text();
    }

    // ennek semmi értelme publikusnak lennie, ha CSS úgyis folyton felülírja
    public void textAlign(TextAlign textAlign) {
        Objects.requireNonNull(textAlign);
        this.textAlign = textAlign;
        invalidate();
    }

    public TextAlign textAlign() {
        return textAlign;
    }

    @Override
    protected Element content() {
        return Layouts.valign(Layouts.VerticalAlignment.CENTER, Layouts.halign(textAlign.asHAlign, textNode));
    }

    @Override
    public String elementName() {
        return "Label";
    }

    @Override
    public <T> void setProperty(CSSProperty<T> property, T value, DecorationContext decorationContext) {
        if (property.equals(CSSProperty.textAlign))
            textAlign(value == null ? DEFAULT_TEXT_ALIGN : (TextAlign) value);
        textNode.setProperty(property, value, decorationContext);
    }

    public enum TextAlign {

        LEFT(Layouts.HorizontalAlignment.LEFT),
        CENTER(Layouts.HorizontalAlignment.CENTER),
        RIGHT(Layouts.HorizontalAlignment.RIGHT);

        final Layouts.HorizontalAlignment asHAlign;

        TextAlign(Layouts.HorizontalAlignment asHAlign) {
            this.asHAlign = asHAlign;
        }
    }
}
