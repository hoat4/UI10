package ui10.decoration.views;

import ui10.base.Element;
import ui10.controls.dialog.DialogView;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.css.DecorBox;
import ui10.decoration.css.ElementMirror;

public class DialogCSS extends CSSStyle<DialogView> implements DialogView.DialogStyle {

    public DialogCSS(DialogView view, CSSDecorator css) {
        super(view, css);
    }

    @Override
    public Element dialogMain(Element element) {
        ElementMirror elementMirror = ElementMirror.ofClassName(this.elementMirror, "dialog-main");
        return new DecorBox(element, css.ruleOf(elementMirror), dc);
    }

    @Override
    public Element dialogButtonBar(Element element) {
        ElementMirror elementMirror = ElementMirror.ofClassName(this.elementMirror, "dialog-button-bar");
        return new DecorBox(element, css.ruleOf(elementMirror), dc);
    }
}
