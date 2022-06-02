package ui10.control4.controls;

import ui10.base.Element;
import ui10.base.EventContext;
import ui10.base.LayoutContext1;
import ui10.base.RenderableElement;
import ui10.control4.ControlView2;
import ui10.decoration.d3.Decoration;
import ui10.font.TextStyle;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.graphics.ColorFill;
import ui10.image.Color;
import ui10.image.Colors;
import ui10.input.keyboard.KeyTypeEvent;
import ui10.input.keyboard.Keyboard;
import ui10.input.pointer.MouseEvent;
import ui10.layout.BoxConstraints;
import ui10.layout.RectangularLayout;
import ui10.window.Cursor;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static ui10.input.keyboard.Keyboard.StandardFunctionSymbol.*;
import static ui10.layout.Layouts.horizontally;

// .text-field, .text, .caret, .selection
public class TextFieldImpl extends ControlView2<TextField, TextFieldImpl.TextFieldDecoration>
        implements TextField.TextFieldListener {

    private Element caret;
    private final TextElement1 textElement1 = new TextElement1();
    private final TextElement2 textElementSel = new TextElement2();
    private final TextElement3 textNodeAfterSel = new TextElement3();

    private int selectionBegin;

    public TextFieldImpl(TextField model) {
        super(model);
        cursor.set(Cursor.TEXT);
        caret = new ColorFill(Colors.BLACK);
    }

    @Override
    public void textChanged() {
        textElement1.listener().textChanged();
        if (model.selection() != null) {
            textElementSel.listener().textChanged();
            textNodeAfterSel.listener().textChanged();
        }
    }

    @Override
    public void caretPositionChanged() {
        invalidate();
    }

    @Override
    public void selectionChanged() {
        textElement1.listener().textChanged();
        textElementSel.listener().textChanged();
        textNodeAfterSel.listener().textChanged();
    }

    private abstract class TextFieldTextElement extends TextElement {

        protected TextElementListener listener() {
            return super.listener();
        }
    }

    private class TextElement1 extends TextFieldTextElement {

        @Override
        public String text() {
            return model.selection() == null ? model.text() : model.text().substring(0, model.selection().begin());
        }

        @Override
        public Element fill() {
            return new ColorFill(decoration().nonSelectedTextColor());
        }

        @Override
        public TextStyle textStyle() {
            return decoration().textStyle();
        }
    }

    private class TextElement2 extends TextFieldTextElement {

        @Override
        public String text() {
            return model.selection() == null ? null :
                    model.text().substring(model.selection().begin(), model.selection().end());
        }

        @Override
        public Element fill() {
            return new ColorFill(decoration().selectedTextColor());
        }

        @Override
        public TextStyle textStyle() {
            return decoration().textStyle();
        }
    }

    private class TextElement3 extends TextFieldTextElement {

        @Override
        public String text() {
            return model.selection() == null ? null :
                    model.text().substring(model.selection().end());
        }

        @Override
        public Element fill() {
            return new ColorFill(decoration().nonSelectedTextColor());
        }

        @Override
        public TextStyle textStyle() {
            return decoration().textStyle();
        }
    }

    @Override
    protected Element contentImpl() {
        return new RectangularLayout() {
            @Override
            public void enumerateStaticChildren(Consumer<Element> consumer) {
                consumer.accept(textElement1);
                consumer.accept(caret);
                if (model.selection() != null) {
                    consumer.accept(textElementSel);
                    consumer.accept(textNodeAfterSel);
                }
            }

            @Override
            public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
                if (model.selection() == null)
                    return context.preferredSize(textElement1, constraints);
                else
                    return context.preferredSize(horizontally(textElement1, textElementSel, textNodeAfterSel), constraints);
            }

            @Override
            protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
                if (model.selection() != null) {
                    int begin = model.selection().begin();
                    int end = model.selection().end();

                    int beginX = textPosToX(begin), endX = textPosToX(end);

                    Rectangle r = Rectangle.of(size.withWidth(beginX));
                    placer.accept(textElement1, r);
                    r = new Rectangle(r.topRight(), size.withWidth(endX - beginX));
                    placer.accept(textElementSel, r);
                    r = new Rectangle(r.topRight(), size.withWidth(size.width() - endX));
                    placer.accept(textNodeAfterSel, r);
                } else {
                    placer.accept(textElement1, Rectangle.of(size));
                }

                int caretX = textPosToX(model.caretPosition());

                Rectangle caretShape = new Rectangle(caretX, 0, 1, size.height());
                placer.accept(caret, caretShape);
            }

            private int textPosToX(Integer x) {
                return decoration().textStyle().textSize(model.text().substring(0, x)).width();
            }

        };
    }

    @EventHandler
    private void onMousePress(MouseEvent.MousePressEvent event, EventContext context) {
        focusContext().focusedControl.set(this);
        int newPos = pickTextPos(event.point().subtract(relativePos((RenderableElement) textElement1.view())));
        model.caretPosition(newPos);
        this.selectionBegin = newPos;
        model.selection(null);
    }

    @EventHandler
    private void onMouseDrag(MouseEvent.MouseDragEvent event, EventContext context) {
        // TODO ez a kaszt hülyén néz ki, de nem tudom, mi lenne a megoldás
        //      legyen minden view RenderableElement subclassa?

        model.caretPosition(pickTextPos(event.point().subtract(relativePos((RenderableElement) textElement1.view()))));

        int begin = Math.min(selectionBegin, model.caretPosition());
        int end = Math.max(selectionBegin, model.caretPosition());
        model.selection(begin == end ? null : new TextField.Selection(begin, end));
    }

    private int pickTextPos(Point p) {
        int i = textElement1.textLayout().pickTextPos(p);
        int l1 = textElement1.text().length();
        if (model.selection() == null || i < l1)
            return i;

        p = p.subtract(textElement1.textLayout().metrics().width(), 0);
        i = textElementSel.textLayout().pickTextPos(p);

        int l2 = textElementSel.text().length();
        if (i < l2)
            return l1 + i;

        p = p.subtract(textElementSel.textLayout().metrics().width(), 0);
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

    public interface TextFieldDecoration extends Decoration {

        Color nonSelectedTextColor();

        Color selectedTextColor();

        TextStyle textStyle();
    }
}
