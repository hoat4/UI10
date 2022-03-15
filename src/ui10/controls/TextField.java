package ui10.controls;

import ui10.base.*;
import ui10.binding.ScalarProperty;
import ui10.decoration.DecorationContext;
import ui10.decoration.css.CSSProperty;
import ui10.decoration.css.Styleable;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.image.Colors;
import ui10.image.RGBColor;
import ui10.input.InputEvent;
import ui10.input.keyboard.KeyTypeEvent;
import ui10.input.keyboard.Keyboard;
import ui10.input.pointer.MouseEvent;
import ui10.layout.BoxConstraints;
import ui10.decoration.css.CSSClass;
import ui10.graphics.ColorFill;
import ui10.graphics.TextNode;
import ui10.layout.RectangularLayout;
import ui10.window.Cursor;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static ui10.decoration.css.CSSClass.withClass;
import static ui10.input.keyboard.Keyboard.StandardFunctionSymbol.*;

public class TextField extends Control implements Styleable {

    public final ScalarProperty<String> text = ScalarProperty.createWithDefault("TextField.text", "szöveg");
    public final ScalarProperty<Integer> caretPosition = ScalarProperty.createWithDefault("TextField.caretPosition", 0);
    public final ScalarProperty<Selection> selection = ScalarProperty.create("TextField.selection");

    private final TextNode textNode = withClass("text", new TextNode());
    private final ColorFill caret = withClass("caret", new ColorFill().color(Colors.BLACK));
    private final TextNode textNodeSel = withClass("selection", new TextNode());
    private final TextNode textNodeAfterSel = withClass("text", new TextNode());

    private int selectionBegin;

    {
        attributes().add(new CSSClass("text-field"));
        text.subscribe(e -> invalidatePane());
        caretPosition.subscribe(e -> invalidatePane());
        selection.subscribe(e -> invalidatePane());
        cursor.set(Cursor.TEXT);
    }

    @Override
    protected void validate() {
        super.validate();

        if (selection.get() == null) {
            textNode.text(text.get());
            textNodeSel.text(null);
            textNodeAfterSel.text(null);
        } else {
            textNode.text(text.get().substring(0, selection.get().begin));
            textNodeSel.text(text.get().substring(selection.get().begin, selection.get().end));
            textNodeAfterSel.text(text.get().substring(selection.get().end));
        }
    }

    @Override
    public Element content() {
        return new RectangularLayout() {
            @Override
            public void enumerateStaticChildren(Consumer<Element> consumer) {
                consumer.accept(textNode);
                consumer.accept(caret);
                if (selection.get() != null) {
                    consumer.accept(textNodeSel);
                    consumer.accept(textNodeAfterSel);
                }
            }

            @Override
            public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
                return constraints.clamp(textNode.textStyle().textSize(text.get()).size());
            }

            @Override
            protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
                if (selection.get() != null) {
                    int begin = selection.get().begin;
                    int end = selection.get().end;

                    int beginX = textPosToX(begin), endX = textPosToX(end);

                    Rectangle r = Rectangle.of(size.withWidth(beginX));
                    placer.accept(textNode, r);
                    r = new Rectangle(r.topRight(), size.withWidth(endX - beginX));
                    placer.accept(textNodeSel, r);
                    r = new Rectangle(r.topRight(), size.withWidth(size.width() - endX));
                    placer.accept(textNodeAfterSel, r);
                } else {
                    placer.accept(textNode, Rectangle.of(size));
                }

                int caretX = textPosToX(caretPosition.get());

                Rectangle caretShape = new Rectangle(caretX, 0, 1, size.height());
                placer.accept(caret, caretShape);
            }

            private int textPosToX(Integer x) {
                return textNode.textStyle().textSize(text.get().substring(0, x)).width();
            }

        };
    }

    @EventHandler
    private void onMousePress(MouseEvent.MousePressEvent event, EventContext context) {
        focusContext.focusedControl.set(this);
        int newPos = pickTextPos(event.point().subtract(relativePos(textNode)));
        caretPosition.set(newPos);
        this.selectionBegin = newPos;
        selection.set(null);
    }

    @EventHandler
    private void onMouseDrag(MouseEvent.MouseDragEvent event, EventContext context) {
        caretPosition.set(pickTextPos(event.point().subtract(relativePos(textNode))));

        int begin = Math.min(selectionBegin, caretPosition.get());
        int end = Math.max(selectionBegin, caretPosition.get());
        selection.set(begin == end ? null : new Selection(begin, end));
    }

    private int pickTextPos(Point p) {
        int i = textNode.textLayout.pickTextPos(p);
        int l1 = textNode.text().length();
        if (selection.get() == null || i < l1)
            return i;

        p = p.subtract(textNode.textLayout.metrics().width(), 0);
        i = textNodeSel.textLayout.pickTextPos(p);

        int l2 = textNodeSel.text().length();
        if (i < l2)
            return l1 + i;

        p = p.subtract(textNodeSel.textLayout.metrics().width(), 0);
        i = textNodeAfterSel.textLayout.pickTextPos(p);
        return l1 + l2 + i;
    }

    @EventHandler
    private void onKeyType(KeyTypeEvent event, EventContext context) {
        event.symbol().standardSymbol().ifPresent(sym -> {
            if (sym instanceof Keyboard.StandardTextSymbol textSymbol) {
                String s = text.get();
                text.set(s.substring(0, caretPosition.get()) + textSymbol.text() + s.substring(caretPosition.get()));
                caretPosition.set(caretPosition.get() + 1);
            }
        });
    }

    @OnFunctionKey(LEFT)
    public void left() {
        selection.set(null);
        if (caretPosition.get() > 0)
            caretPosition.set(caretPosition.get() - 1);
    }

    @OnFunctionKey(RIGHT)
    public void right() {
        selection.set(null);
        if (caretPosition.get() < text.get().length())
            caretPosition.set(caretPosition.get() + 1);
    }

    @OnFunctionKey(BACKSPACE)
    public void backspace() {
        if (caretPosition.get() > 0) {
            text.set(text.get().substring(0, caretPosition.get() - 1) + text.get().substring(caretPosition.get()));
            caretPosition.set(caretPosition.get() - 1);
        }
    }

    @OnFunctionKey(DELETE)
    public void delete() {
        if (caretPosition.get() < text.get().length())
            text.set(text.get().substring(0, caretPosition.get()) + text.get().substring(caretPosition.get() + 1));
    }

    @Override
    public String elementName() {
        return "TextField";
    }

    @Override
    public <T> void setProperty(CSSProperty<T> property, T value, DecorationContext decorationContext) {
    }

    private record Selection(int begin, int end) {
    }
}
