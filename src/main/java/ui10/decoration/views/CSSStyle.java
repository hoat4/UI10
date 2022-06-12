package ui10.decoration.views;

import ui10.base.Element;
import ui10.base.EnduringElement;
import ui10.decoration.*;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.css.CSSProperty;
import ui10.decoration.css.Length;
import ui10.decoration.css.Rule;

public class CSSStyle<V extends EnduringElement> implements Style {

    protected final V view;
    private final CSSDecorator css;
    protected final Rule rule;

    protected DecorationContext dc;

    final ElementMirrorImpl elementMirror;

    public CSSStyle(V view, CSSDecorator css) {
        this.view = view;
        this.css = css;
        elementMirror = new ElementMirrorImpl((StyleableContainer<?>) view);
        this.rule = css.ruleOf(elementMirror);
        elementMirror.installListeners();

        dc = new DecorationContext(view, findEmSize(view, css));
    }

    protected Fill textFill() {
        EnduringElement e = view;
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

    private static int findEmSize(EnduringElement e, CSSDecorator css) {
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

    @Override
    public Element wrapContent(Element controlContent) {
        return rule.apply2(controlContent, dc);
    }

    @Override
    public DecorationContext decorationContext() {
        return dc;
    }
}
