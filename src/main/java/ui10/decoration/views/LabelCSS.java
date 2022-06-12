package ui10.decoration.views;

import ui10.base.Element;
import ui10.base.TextAlign;
import ui10.decoration.Fill;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.css.CSSProperty;
import ui10.decoration.css.Length;
import ui10.font.TextStyle;
import ui10.shell.renderer.java2d.AWTTextStyle;

import static ui10.decoration.css.Length.em;

public class LabelCSS extends CSSStyle<StyleableLabelView> implements StyleableLabelView.LabelStyle {

    public LabelCSS(StyleableLabelView label, CSSDecorator css) {
        super(label, css);
    }

    @Override
    public Element wrapContent(Element controlContent) {
        return controlContent;
    }

    @Override
    public TextAlign textAlign() {
        return rule.get(CSSProperty.textAlign);
    }

    @Override
    public Fill textFill() {
        return rule.get(CSSProperty.textColor);
    }

    @Override
    public TextStyle textStyle() {
        Length len = rule.get(CSSProperty.fontSize);
        if (len == null)
            len = em(1);
        return AWTTextStyle.of(dc.length(len), false);
    }
}
