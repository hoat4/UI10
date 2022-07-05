package ui10.decoration.views;

import ui10.base.Element;
import ui10.controls.TextAlign;
import ui10.decoration.Fill;
import ui10.decoration.css.*;
import ui10.font.TextStyle;
import ui10.layout.Layouts;
import ui10.shell.renderer.java2d.AWTTextStyle;

import java.util.Optional;

import static ui10.decoration.css.Length.em;

public class TextViewCSS extends CSSStyle<StyleableLabelView> implements StyleableLabelView.TextViewStyle {

    private final Rule selectionRule;

    public TextViewCSS(StyleableLabelView label, CSSDecorator css) {
        super(label, css);

        ElementMirror selectionElementMirror = new ElementMirror() {

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
                return TextViewCSS.this.elementMirror;
            }
        };
        selectionRule = css.ruleOf(selectionElementMirror);
    }

    @Override
    public Element wrapContent(Element controlContent) {
        return controlContent;
    }

    @Override
    public TextAlign textAlign() {
        return rule.get(CSSProperty.textAlign);
    }

    @Override
    public Fill textFill() {
        return rule.get(CSSProperty.textColor);
    }

    @Override
    public TextStyle textStyle() {
        Length len = rule.get(CSSProperty.fontSize);
        if (len == null)
            len = em(1);
        return AWTTextStyle.of(dc.length(len), false);
    }

    @Override
    public TextViewPartDecoration nonSelectedPart() {
        return new TextViewPartDecoration() {
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
    public TextViewPartDecoration selectedPart() {
        return new TextViewPartDecoration() {
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
