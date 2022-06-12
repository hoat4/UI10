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
    private Element selectionFill;

    private int selectionBegin;

    public StyleableTextFieldView(TextField model) {
        super(model);
        //cursor.set(Cursor.TEXT);
    }

    @Override
    protected void onDecorationChanged() {
        selectionFill = decoration().selectedPart().background().makeElement(decoration().decorationContext());
    }

    @Override
    public void textChanged() {
        invalidate();
    }

    @Override
    public void caretPositionChanged() {
        invalidate();
    }

    @Override
    public void selectionChanged() {
        invalidate();
    }

    @Override
    protected void validate() {
        super.validate();

        if (model.selection() == null) {
            textNode.text(model.text());
            textNodeSel.text(null);
            textNodeAfterSel.text(null);
        } else {
            textNode.text(model.text().substring(0, model.selection().begin()));
            textNodeSel.text(model.text().substring(model.selection().begin(), model.selection().end()));
            textNodeAfterSel.text(model.text().substring(model.selection().end()));
        }
        textNode.fill(decoration().nonSelectedPart().foreground().makeElement(decoration().decorationContext()));
        textNodeSel.fill(decoration().selectedPart().foreground().makeElement(decoration().decorationContext()));
        textNodeAfterSel.fill(decoration().nonSelectedPart().foreground().makeElement(decoration().decorationContext()));
        textNode.textStyle(decoration().textStyle());
        textNodeSel.textStyle(decoration().textStyle());
        textNodeAfterSel.textStyle(decoration().textStyle());
    }

    @Override
    protected Element contentImpl() {
        return new RectangularLayout() {
            @Override
            public void enumerateStaticChildren(Consumer<Element> consumer) {
                consumer.accept(textNode);
                consumer.accept(caret);
                consumer.accept(selectionFill);
                if (model.selection() != null) {
                    consumer.accept(textNodeSel);
                    consumer.accept(textNodeAfterSel);
                }
            }

            @Override
            public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
                    Size caretWidth = new Size(1, 0);
                if (model.selection() == null) {
                    return context.preferredSize(textNode, constraints.subtract(caretWidth)).add(caretWidth);
                } else
                    return context.preferredSize(horizontally(textNode, textNodeSel, textNodeAfterSel),
                            constraints.subtract(caretWidth)).add(caretWidth);
            }

            @Override
            protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
                if (model.selection() != null) {
                    int begin = model.selection().begin();
                    int end = model.selection().end();

                    int beginX = textPosToX(begin), endX = textPosToX(end);

                    Rectangle r = Rectangle.of(size.withWidth(beginX));
                    placer.accept(textNode, r);
                    r = new Rectangle(r.topRight(), size.withWidth(endX - beginX));
                    placer.accept(selectionFill, r);
                    placer.accept(textNodeSel, r);
                    r = new Rectangle(r.topRight(), size.withWidth(size.width() - endX));
                    placer.accept(textNodeAfterSel, r);
                } else {
                    placer.accept(textNode, Rectangle.of(size));
                }

                int caretX = textPosToX(model.caretPosition());

                Rectangle caretShape = new Rectangle(caretX, 0, 1, size.height());
                placer.accept(caret, caretShape);
            }

            private int textPosToX(Integer x) {
                return textNode.textStyle().textSize(model.text().substring(0, x)).width();
            }

        };
    }

    private Point relativePos(EnduringElement e) {
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
}
