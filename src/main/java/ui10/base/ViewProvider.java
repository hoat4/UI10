package ui10.base;

import ui10.control4.ControlView2;
import ui10.control4.controls.*;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.d3.Decoration;

public class ViewProvider {

    private final CSSDecorator css;

    public ViewProvider(CSSDecorator css) {
        this.css = css;
    }

    public Element makeView(ElementModel<?> model) {
        if (model instanceof LabelModel m)
            return new LabelImpl(m);
        if (model instanceof TextElement m)
            return new TextElementImpl(m);
        if (model instanceof TextField m)
            return new TextFieldImpl(m);
        if (model instanceof ButtonModel m)
            return new ButtonView2(m);
        throw new UnsupportedOperationException("unknown element: " + model);
    }

    public <D extends Decoration> D makeDecoration(ControlView2<?, D> view) {
        if (view instanceof ButtonView2 v)
            return (D) new ButtonDec(v, css);
        if (view instanceof TextFieldImpl)
            return (D) new TFDec();
        if (view instanceof LabelImpl v)
            return (D) new LabelDec(v, css);
        throw new UnsupportedOperationException("unknown view: " + view);
    }
}
