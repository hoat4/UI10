package ui10.control4.controls;

import ui10.base.Element;
import ui10.control4.ControlView2;
import ui10.font.TextStyle;
import ui10.image.Color;
import ui10.image.Colors;
import ui10.shell.renderer.java2d.AWTTextStyle;

public class TFDec implements TextFieldImpl.TextFieldDecoration{
    @Override
    public Color nonSelectedTextColor() {
        return Colors.BLACK;
    }

    @Override
    public Color selectedTextColor() {
        return Colors.WHITE;
    }

    @Override
    public TextStyle textStyle() {
        return AWTTextStyle.of(12, false);
    }

    @Override
    public Element wrapContent(Element controlContent) {
        return controlContent;
    }
}
