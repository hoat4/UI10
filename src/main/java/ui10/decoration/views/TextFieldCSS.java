package ui10.decoration.views;

import ui10.decoration.css.CSSDecorator;
import ui10.font.TextStyle;
import ui10.shell.renderer.java2d.AWTTextStyle;

import static ui10.decoration.css.Length.em;

public class TextFieldCSS extends CSSStyle<StyleableTextFieldView> implements StyleableTextFieldView.TextFieldStyle {

    public TextFieldCSS(StyleableTextFieldView view, CSSDecorator css) {
        super(view, css);
    }

    @Override
    public TextStyle textStyle() {
        return AWTTextStyle.of(dc.length(em(1)), false);
    }
}
