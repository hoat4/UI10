package ui10.decoration.views;

import ui10.base.*;
import ui10.controls.TextElement;
import ui10.controls.TextField;
import ui10.decoration.Style;
import ui10.decoration.Fill;
import ui10.font.TextStyle;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.graphics.ColorFill;
import ui10.image.Colors;
import ui10.input.keyboard.KeyTypeEvent;
import ui10.input.keyboard.Keyboard;
import ui10.input.pointer.MouseEvent;
import ui10.layout.BoxConstraints;
import ui10.layout.LinearLayout;
import ui10.layout.RectangularLayout;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static ui10.input.keyboard.Keyboard.StandardFunctionSymbol.*;
import static ui10.layout.Layouts.horizontally;

// .text-field, .text, .caret, .selection
public class StyleableTextFieldView extends StyleableView<TextField, StyleableTextFieldView.TextFieldStyle>
        implements TextField.TextFieldListener, InputHandler {

    private final TextElement textNode = new TextElement();
    private final ColorFill caret = new ColorFill().color(Colors.BLACK);
    private final TextElement textNodeSel = new TextElement();
    private final TextElement textNodeAfterSel = new TextElement();
    private final TextFieldContent content = new TextFieldContent();
    private Element selectionFill;

    private int selectionBegin;

    public StyleableTextFieldView(TextField model) {
        super(model);
        //cursor.set(Cursor.TEXT);
    }

    @Override
    protected void onDecorationChanged() {
        selectionFill = decoration().selectedPart().background().makeElement(decoration().decorationContext());
        textNode.fill(decoration().nonSelectedPart().foreground().makeElement(decoration().decorationContext()));
        textNodeSel.fill(decoration().selectedPart().foreground().makeElement(decoration().decorationContext()));
        textNodeAfterSel.fill(decoration().nonSelectedPart().foreground().makeElement(decoration().decorationContext()));
        textNode.textStyle(decoration().textStyle());
        textNodeSel.textStyle(decoration().textStyle());
        textNodeAfterSel.textStyle(decoration().textStyle());
    }

    @Setup
    private void updateTexts() {
        if (model.selection() == null) {
            textNode.text(model.text());
            textNodeSel.text(null);
            textNodeAfterSel.text(null);
        } else {
            textNode.text(model.text().substring(0, model.selection().begin()));
            textNodeSel.text(model.text().substring(model.selection().begin(), model.selection().end()));
            textNodeAfterSel.text(model.text().substring(model.selection().end()));
        }
    }

    @Override
    public void textChanged() {
        updateTexts();
        content.refresh();
    }

    @Override
    public void caretPositionChanged() {
        content.refresh();
    }

    @Override
    public void selectionChanged() {
        updateTexts();
        content.refresh();
    }

    @Override
    protected Element contentImpl() {
        return content;
    }

    private Point relativePos(Element e) {
        return e.origin().subtract(origin());
    }

    @EventHandler
    private void onMousePress(MouseEvent.MousePressEvent event, EventContext context) {
        focusContext().focusedControl.set(this);
        int newPos = pickTextPos(event.point().subtract(relativePos(textNode)));
        model.caretPosition(newPos);
        this.selectionBegin = newPos;
        model.selection(null);
    }

    @EventHandler
    private void onMouseDrag(MouseEvent.MouseDragEvent event, EventContext context) {
        model.caretPosition(pickTextPos(event.point().subtract(relativePos(textNode))));

        int begin = Math.min(selectionBegin, model.caretPosition());
        int end = Math.max(selectionBegin, model.caretPosition());
        model.selection(begin == end ? null : new TextField.Selection(begin, end));
    }

    private int pickTextPos(Point p) {
        int i = textNode.textLayout().pickTextPos(p);
        int l1 = textNode.text().length();
        if (model.selection() == null || i < l1)
            return i;

        p = p.subtract(textNode.textLayout().metrics().width(), 0);
        i = textNodeSel.textLayout().pickTextPos(p);

        int l2 = textNodeSel.text().length();
        if (i < l2)
            return l1 + i;

        p = p.subtract(textNodeSel.textLayout().metrics().width(), 0);
        i = textNodeAfterSel.textLayout().pickTextPos(p);
        return l1 + l2 + i;
    }

    @EventHandler
    private void onKeyType(KeyTypeEvent event, EventContext context) {
        event.symbol().standardSymbol().ifPresent(sym -> {
            if (sym instanceof Keyboard.StandardTextSymbol textSymbol) {
                String s = model.text();
                model.text(s.substring(0, model.caretPosition()) + textSymbol.text() + s.substring(model.caretPosition()));
                model.caretPosition(model.caretPosition() + 1);
            }
        });
    }

    @OnFunctionKey(LEFT)
    public void left() {
        model.selection(null);
        if (model.caretPosition() > 0)
            model.caretPosition(model.caretPosition() - 1);
    }

    @OnFunctionKey(RIGHT)
    public void right() {
        model.selection(null);
        if (model.caretPosition() < model.text().length())
            model.caretPosition(model.caretPosition() + 1);
    }

    @OnFunctionKey(BACKSPACE)
    public void backspace() {
        if (model.caretPosition() > 0) {
            model.text(model.text().substring(0, model.caretPosition() - 1) + model.text().substring(model.caretPosition()));
            model.caretPosition(model.caretPosition() - 1);
        }
    }

    @OnFunctionKey(DELETE)
    public void delete() {
        if (model.caretPosition() < model.text().length())
            model.text(model.text().substring(0, model.caretPosition()) + model.text().substring(model.caretPosition() + 1));
    }

    public interface TextFieldStyle extends Style {

        TextStyle textStyle();

        TextFieldPartDecoration nonSelectedPart();

        TextFieldPartDecoration selectedPart();

        interface TextFieldPartDecoration {

            Fill foreground();

            Fill background();
        }
    }

    private class TextFieldContent extends RectangularLayout {

        private final LinearLayout hbox = horizontally(textNode, textNodeSel, textNodeAfterSel);

        void refresh() {
            listener().layoutInvalidated();
        }

        @Override
        public void enumerateChildren(Consumer<Element> consumer) {
            consumer.accept(hbox);
            consumer.accept(caret);
            consumer.accept(selectionFill);
        }

        @Override
        public Size preferredSize(BoxConstraints constraints, LayoutContext1 context) {
            Size caretWidth = new Size(1, 0);
            return context.preferredSize(hbox, constraints.subtract(caretWidth)).add(caretWidth);
        }

        @Override
        protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
            if (model.selection() != null) {
                int begin = model.selection().begin();
                int end = model.selection().end();

                int beginX = textPosToX(begin), endX = textPosToX(end);

                Rectangle r = Rectangle.of(size.withWidth(beginX));
                r = Rectangle.of(r.topLeft().addX(beginX), r.bottomLeft().addX(endX));
                placer.accept(selectionFill, r);
            }
            placer.accept(hbox, Rectangle.of(size));

            int caretX = textPosToX(model.caretPosition());

            Rectangle caretShape = new Rectangle(caretX, 0, 1, size.height());
            placer.accept(caret, caretShape);
        }

        private int textPosToX(Integer x) {
            return textNode.textStyle().textSize(model.text().substring(0, x)).width();
        }

    }
}
