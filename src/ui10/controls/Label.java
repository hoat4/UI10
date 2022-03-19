package ui10.controls;

import ui10.base.Control;
import ui10.base.Element;
import ui10.binding2.Property;
import ui10.decoration.css.CSSClass;
import ui10.graphics.TextNode;
import ui10.layout.Layouts;

import java.util.Objects;

public class Label extends Control  {

    public static final Property<TextAlign> TEXT_ALIGN_PROPERTY = new Property<>();

    private TextNode textNode = new TextNode();

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

    @Override
    protected Element content() {
        TextAlign textAlign = getProperty(TEXT_ALIGN_PROPERTY);
        return Layouts.valign(Layouts.VerticalAlignment.CENTER, Layouts.halign(textAlign.asHAlign, textNode));
    }

    @Override
    public String elementName() {
        return "Label";
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
