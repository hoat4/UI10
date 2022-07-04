package ui10.decoration.views;

import ui10.base.Element;
import ui10.binding9.Bindings;
import ui10.decoration.*;
import ui10.decoration.css.*;

public class CSSStyle<V extends Element> implements Style {

    protected final V view;
    protected final CSSDecorator css;
    protected Rule rule;

    protected DecorationContext dc;

    protected final ElementMirrorImpl elementMirror;

    public CSSStyle(V view, CSSDecorator css) {
        this.view = view;
        this.css = css;
        elementMirror = new ElementMirrorImpl((StyleableContainer<?>) view);

        this.rule = Bindings.onInvalidated(() -> css.ruleOf(elementMirror), this::refresh);

        dc = new DecorationContext(view, findEmSize(view, css));
    }

    protected Fill textFill() {
        Element e = view;
        while (true) {
            if (e instanceof StyleableView) {
                ElementMirrorImpl elementMirror = new ElementMirrorImpl((StyleableView<?, ?>) e);
                Rule r = css.ruleOf(elementMirror);
                Fill l = r.get(CSSProperty.textColor);
                if (l != null)
                    return l;
            }
            e = e.parent();
        }
    }

    private static int findEmSize(Element e, CSSDecorator css) {
        int scale = 1 << 14;
        while (true) {
            if (e instanceof StyleableView) {
                ElementMirrorImpl elementMirror = new ElementMirrorImpl((StyleableView<?, ?>) e);
                Rule r = css.ruleOf(elementMirror);
                Length l = r.get(CSSProperty.fontSize);
                if (l != null) {
                    if (l.em() == 0 && l.relative() == 0)
                        return l.px() >> 14;
                    else
                        scale = scale * (l.em() + l.relative() >> 7) >> 7;
                }
            }
            e = e.parent();
        }
    }

    private DecorBox decorBox;

    @Override
    public Element wrapContent(Element controlContent) {
        if (rule.needsDecorBox()) {
            if (decorBox == null)
                decorBox = new DecorBox(controlContent, rule, dc);
            return decorBox;
        } else {
            decorBox = null; // ???
            return controlContent;
        }
    }

    @Override
    public DecorationContext decorationContext() {
        return dc;
    }

    public void refresh() {
        this.rule = Bindings.onInvalidated(() -> css.ruleOf(elementMirror), this::refresh);

        if (rule.needsDecorBox() != (decorBox != null)) {
            throw new UnsupportedOperationException();
        }

        if (decorBox != null) {
            decorBox.rule = rule;
            decorBox.invalidationPoint.invalidate();
        }
    }
}
