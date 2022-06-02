package ui10.control4.controls;

import ui10.base.Element;
import ui10.control4.ControlView2;
import ui10.decoration.d3.Decoration;
import ui10.font.TextStyle;
import ui10.graphics.ColorFill;
import ui10.image.Color;
import ui10.layout.Layouts;

// TODO text-align legyen állítható CSS-ből
// .label
public class LabelImpl extends ControlView2<LabelModel, LabelImpl.LabelDecoration> implements LabelModel.LabelModelListener {

    private final LabelTextElement textElement = new LabelTextElement(); // .label-text

    public LabelImpl(LabelModel model) {
        super(model);
        textChanged();
    }

    @Override
    protected Element contentImpl() {
        return textElement;
    }

    @Override
    public void textChanged() {
        textElement.listener().textChanged();
    }

    public enum TextAlign {

        LEFT(Layouts.HorizontalAlignment.LEFT),
        CENTER(Layouts.HorizontalAlignment.CENTER),
        RIGHT(Layouts.HorizontalAlignment.RIGHT);

        final Layouts.HorizontalAlignment asHAlign;

        TextAlign(Layouts.HorizontalAlignment asHAlign) {
            this.asHAlign = asHAlign;
        }
    }

    private class LabelTextElement extends TextElement {

        protected TextElementListener listener() {
            return super.listener();
        }

        @Override
        public String text() {
            return model.text();
        }

        @Override
        public Element fill() {
            return new ColorFill(decoration().textColor());
        }

        @Override
        public TextStyle textStyle() {
            return decoration().textStyle();
        }
    }

    public interface LabelDecoration extends Decoration {

        Color textColor();

        TextStyle textStyle();
    }
}
