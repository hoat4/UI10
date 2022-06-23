package ui10.decoration.views;

import ui10.base.Element;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.css.DecorBox;
import ui10.decoration.css.ElementMirror;
import ui10.layout.Layouts;
import ui10.layout.LinearLayout;

import static ui10.layout.Layouts.empty;

public class TabbedPaneCSS extends CSSStyle<StyleableTabbedPaneView> implements StyleableTabbedPaneView.TabbedPaneStyle {

    public TabbedPaneCSS(StyleableTabbedPaneView view, CSSDecorator css) {
        super(view, css);
    }

    @Override
    public Element tabButtons(LinearLayout element) {
        element.gap = 2; // TODO
        return new DecorBox(element, css.ruleOf(ElementMirror.ofElementName(elementMirror, "TabButtons")), dc);
    }

    @Override
    public Element tabHeaderArea(Element element) {
        ElementMirror tabHeaderAreaMirror = ElementMirror.ofElementName(elementMirror, "TabHeaderArea");
        ElementMirror tabHeaderBackgroundMirror = ElementMirror.ofElementName(tabHeaderAreaMirror, "TabHeaderBackground");
        Element bg = new DecorBox(empty(), css.ruleOf(tabHeaderBackgroundMirror), dc);
        return new DecorBox(
                Layouts.stack(bg, element),
                css.ruleOf(tabHeaderAreaMirror),
                dc
        );
    }
}
