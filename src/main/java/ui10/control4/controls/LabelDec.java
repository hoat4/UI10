package ui10.control4.controls;

import ui10.base.Element;
import ui10.control4.ControlView2;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.css.Selector;
import ui10.decoration.d3.Decoration;
import ui10.font.TextStyle;
import ui10.image.Color;
import ui10.image.Colors;
import ui10.shell.renderer.java2d.AWTTextStyle;

public class LabelDec implements LabelImpl.LabelDecoration {

    private final LabelImpl label;
    private final CSSDecorator css;

    public LabelDec(LabelImpl label, CSSDecorator css) {
        this.label = label;
        this.css = css;
    }

    @Override
    public Element wrapContent(Element controlContent) {
        return controlContent;
    }

    @Override
    public Color textColor() {
        return Colors.BLACK;
    }

    @Override
    public TextStyle textStyle() {
        return AWTTextStyle.of(20, false);
    }
}
