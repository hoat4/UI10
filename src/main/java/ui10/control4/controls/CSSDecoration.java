package ui10.control4.controls;

import ui10.base.Element;
import ui10.base.EnduringElement;
import ui10.decoration.DecorationContext;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.css.Rule;
import ui10.decoration.d3.Decoration;

public abstract class CSSDecoration<V extends EnduringElement> implements Decoration {

    private final V view;
    private final Rule rule;

    public CSSDecoration(V view, CSSDecorator css) {
        this.view = view;
        this.rule = null;
    }

    @Override
    public Element wrapContent(Element controlContent) {
        DecorationContext dc =  new DecorationContext(view);
        return rule.apply2(controlContent, dc);
    }
}
