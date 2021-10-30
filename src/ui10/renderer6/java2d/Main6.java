package ui10.renderer6.java2d;

import ui10.geom.Insets;
import ui10.image.RGBColor;
import ui10.ui6.graphics.ColorFill;
import ui10.ui6.layout.Layouts;
import ui10.ui6.controls.TextField;
import ui10.ui6.window.Window;

public class Main6 {

    public static void main(String[] args) {
        AWTDesktop desktop = new AWTDesktop();
        TextField tf = new TextField();

        //Layouts.roundRectangle(new ColorFill(RGBColor.YELLOW), 20),
        Window window = Window.of(
                Layouts.padding(
                        Layouts.stack(
                                new ColorFill(RGBColor.YELLOW),
                                Layouts.padding(
                                        Layouts.shaped(
                                                Layouts.roundRectangle(new ColorFill(RGBColor.GREEN), 20)
                                        ),
                                        new Insets(50)
                                )
                        ),
                        new Insets(50)
                )
        );
        desktop.windows.add(window);
    }


}
