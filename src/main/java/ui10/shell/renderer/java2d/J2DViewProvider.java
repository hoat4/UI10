package ui10.shell.renderer.java2d;

import ui10.base.Container;
import ui10.base.Element;
import ui10.base.ElementModel;
import ui10.base.ViewProvider;
import ui10.controls.TextElement;
import ui10.graphics.ColorFill;
import ui10.graphics.LinearGradient;
import ui10.graphics.Opacity;

public class J2DViewProvider implements ViewProvider {

    private final J2DRenderer renderer;

    public J2DViewProvider(J2DRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Element makeView(ElementModel<?> n) {
        if (n instanceof ColorFill f)
            return new J2DColorFillElement(renderer, f);

        if (n instanceof Container d)
            return new J2DContainer(renderer, d);

        if (n instanceof TextElement t)
            return new J2DTextElement(renderer, t);

        if (n instanceof LinearGradient l)
            return new J2DLinearGradient(renderer, l);

        if (n instanceof Opacity o)
            return new J2DOpacityElement(renderer, o);

        return null;
    }
}
