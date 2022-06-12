package ui10.decoration.views;

import ui10.decoration.Fill;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.css.CSSProperty;
import ui10.decoration.css.ElementMirror;
import ui10.decoration.css.Rule;
import ui10.font.TextStyle;
import ui10.layout.Layouts;
import ui10.shell.renderer.java2d.AWTTextStyle;

import java.util.Optional;

import static ui10.decoration.css.Length.em;

public class TextFieldCSS extends CSSStyle<StyleableTextFieldView> implements StyleableTextFieldView.TextFieldStyle {

    private Rule selectionRule;

    public TextFieldCSS(StyleableTextFieldView view, CSSDecorator css) {
        super(view, css);

        ElementMirror elementMirror = new ElementMirror() {

            @Override
            public boolean isPseudoElement(String pseudoElementName) {
                return pseudoElementName.equals("selection");
            }

            @Override
            public Optional<Integer> indexInSiblings() {
                return Optional.empty();
            }

            @Override
            public ElementMirror parent() {
                return TextFieldCSS.this.elementMirror;
            }
        };
        selectionRule = css.ruleOf(elementMirror);
    }

    @Override
    public TextStyle textStyle() {
        return AWTTextStyle.of(dc.length(em(1)), false);
    }

    @Override
    public TextFieldPartDecoration nonSelectedPart() {
        return new TextFieldPartDecoration() {
            @Override
            public Fill foreground() {
                return textFill();
            }

            @Override
            public Fill background() {
                return dc-> Layouts.empty();
            }
        };
    }

    @Override
    public TextFieldPartDecoration selectedPart() {
        return new TextFieldPartDecoration() {
            @Override
            public Fill foreground() {
                return selectionRule.get(CSSProperty.textColor);
            }

            @Override
            public Fill background() {
                return selectionRule.get(CSSProperty.background);
            }
        };
    }
}
