package ui10.decoration.views;

import ui10.base.Element;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.css.DecorBox;
import ui10.decoration.css.ElementMirror;

public class TabButtonCSS extends CSSStyle<StyleableTabbedPaneView.TabButton> implements StyleableTabbedPaneView.TabButton.TabButtonStyle {

    public TabButtonCSS(StyleableTabbedPaneView.TabButton view, CSSDecorator css) {
        super(view, css);
    }

    @Override
    public Element wrapContent(Element controlContent) {
        ElementMirror tabButtonInner = ElementMirror.ofElementName(elementMirror, "TabButtonInner");
        controlContent = new DecorBox(controlContent, css.ruleOf(tabButtonInner), dc);
        return super.wrapContent(controlContent);
    }

    @Override
    public void selectedChanged() {
        refresh();
    }
}
