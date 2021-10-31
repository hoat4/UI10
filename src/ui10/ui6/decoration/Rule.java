package ui10.ui6.decoration;

import ui10.geom.Insets;
import ui10.geom.Size;
import ui10.image.Color;
import ui10.ui6.Element;
import ui10.ui6.graphics.ColorFill;

import static ui10.ui6.layout.Layouts.*;

public class Rule {

    private Color background;
    private Length margin, cornerRadius;
    private Length minWidth, minHeight;
    private BorderSpec border;

    void parseProperty(String name, CSSParser parser) {
        switch (name) {
            case "background" -> background = parser.parseColor();
            case "margin" -> margin = parser.parseLength();
            case "border-radius" -> cornerRadius = parser.parseLength();
            case "min-width" -> minWidth = parser.parseLength();
            case "min-height" -> minHeight = parser.parseLength();
            case "border" -> border = parser.parseBorder();
            default -> throw new UnsupportedOperationException("unknown CSS property: " + name);
        }
    }

    public Element apply(Element e, DecorationContext context) {
        if (background != null)
            e = stack(new ColorFill(background), e);

        if (cornerRadius != null)
            e = roundRectangle(context.length(cornerRadius), e);

        if (border != null)
            e = new Border(new Insets(context.length(border.len())), new ColorFill(border.color()), e);

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
