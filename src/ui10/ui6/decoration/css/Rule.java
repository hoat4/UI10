package ui10.ui6.decoration.css;

import ui10.geom.Insets;
import ui10.geom.Size;
import ui10.renderer.java2d.AWTTextStyle;
import ui10.ui6.Element;
import ui10.ui6.decoration.Border;
import ui10.ui6.decoration.BorderSpec;
import ui10.ui6.decoration.DecorationContext;
import ui10.ui6.decoration.Fill;
import ui10.ui6.graphics.TextNode;

import static ui10.ui6.layout.Layouts.*;

public class Rule {

    private Fill background, textColor;
    private Length margin, padding, cornerRadius;
    private Length minWidth, minHeight;
    private BorderSpec border;
    private Length fontSize;

    void parseProperty(String name, CSSParser parser) {
        switch (name) {
            case "background" -> background = parser.parseFill();
            case "color" -> textColor = parser.parseFill();
            case "margin" -> margin = parser.parseLength();
            case "padding" -> padding = parser.parseLength();
            case "border-radius" -> cornerRadius = parser.parseLength();
            case "min-width" -> minWidth = parser.parseLength();
            case "min-height" -> minHeight = parser.parseLength();
            case "border" -> border = parser.parseBorder();
            case "font-size" -> fontSize = parser.parseLength();
            default -> throw new UnsupportedOperationException("unknown CSS property: " + name);
        }
    }

    public void apply1(Element e, DecorationContext context) {
        if (fontSize != null)
            ((TextNode) e).textStyle(AWTTextStyle.of(context.length(fontSize)));

        if (textColor != null)
            ((TextNode) e).fill(textColor.makeElement(context));
    }

    public Element apply2(Element e, DecorationContext context) {
        if (padding != null)
            e = padding(e, new Insets(context.length(padding)));

        if (background != null)
            e = stack(background.makeElement(context), e);

        if (cornerRadius != null)
            e = roundRectangle(context.length(cornerRadius), e);

        if (border != null)
            e = new Border(new Insets(context.length(border.len())), border.fill().makeElement(context), e);

        if (minWidth != null || minHeight != null)
            e = minSize(e, new Size(
                    minWidth == null ? 0 : context.length(minWidth),
                    minHeight == null ? 0 : context.length(minHeight)
            ));

        if (margin != null)
            e = padding(e, new Insets(context.length(margin)));

        return e;
    }
}
