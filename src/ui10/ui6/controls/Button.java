package ui10.ui6.controls;

import ui10.binding.ScalarProperty;
import ui10.geom.Insets;
import ui10.image.Colors;
import ui10.image.RGBColor;
import ui10.input.InputEvent;
import ui10.input.pointer.MouseEvent;
import ui10.renderer.java2d.AWTTextStyle;
import ui10.ui6.*;
import ui10.ui6.graphics.ColorFill;
import ui10.ui6.graphics.LinearGradient;
import ui10.ui6.graphics.TextNode;
import ui10.ui6.layout.Layouts;

import static ui10.ui6.layout.Layouts.padding;

public class Button extends Control {

    static final RGBColor fxbase = RGBColor.ofRGB(0xECECEC);
    static final RGBColor fxbg = fxbase.derive(.264);

    static final RGBColor fxctrlinnerbg = Colors.WHITE;
    static final RGBColor fxmidtextcolor = RGBColor.ofRGBShort(0x333);
    static final RGBColor textColor = fxmidtextcolor; // ladderral számolja bg-ből
    static final RGBColor fxtextboxborder = fxbg.derive(-.15);
    static final RGBColor buttonfxcolor = fxbase;
    static final RGBColor buttonfxouterborder = buttonfxcolor.derive(-.23);
    static final RGBColor buttonfxouterborder_pressed = buttonfxcolor.derive(-.06).derive(-.23);

    private boolean _pressed;

    private final TextNode textNode = new TextNode();

    @Override
    public void validate() {
        super.validate();
        textNode.text("Gomb").textStyle(AWTTextStyle.of(20)).fill(new ColorFill(Colors.BLACK));
    }

    public ScalarProperty<Boolean> pressed() {
        return property((Button b) -> b._pressed, (b, v) -> b._pressed = v);
    }

    @Override
    public Element content() {
        return Layouts.stack(
                Layouts.roundRectangle(3, new ColorFill(RGBColor.ofIntRGBA(0xFFFFFFBA))),
                padding(
                        Layouts.roundRectangle(3, new ColorFill(buttonfxouterborder)),
                        new Insets(0, 0, 1, 0)
                ),
                padding(
                        Layouts.roundRectangle(
                                2, LinearGradient.vertical(
                                        RGBColor.ofRGB(0xFDFDFD).derive(pressed().get() ? -.06 : 0),
                                        RGBColor.ofRGB(0xE1E1E1).derive(pressed().get() ? -.06 : 0)
                                )
                        ), new Insets(1, 1, 2, 1)
                ),
                padding(
                        Layouts.roundRectangle(
                                1, LinearGradient.vertical(
                                        RGBColor.ofRGB(pressed().get() ? 0xE2E2E2 : 0xEFEFEF),
                                        RGBColor.ofRGB(pressed().get() ? 0xCDCDCD : 0xD9D9D9)
                                )
                        ),
                        new Insets(2, 2, 3, 2)
                ),
                padding(textNode, new Insets(4, 8))
        );
    }

    @Override
    public boolean bubble(InputEvent event) {
        if (event instanceof MouseEvent.MousePressEvent)
            pressed().set(true);
        if (event instanceof MouseEvent.MouseReleaseEvent)
            pressed().set(false);
        return false;
    }

}