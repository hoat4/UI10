package ui10.decoration.views;

import ui10.base.ContentEditable;
import ui10.base.Element;
import ui10.base.LayoutContext1;
import ui10.controls.InputField;
import ui10.decoration.Style;
import ui10.font.TextStyle;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.graphics.ColorFill;
import ui10.image.Colors;
import ui10.input.Event;
import ui10.layout.BoxConstraints;
import ui10.layout.RectangularLayout;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static ui10.input.keyboard.KeySymbol.StandardFunctionSymbol.*;

// .text-field, .text, .caret, .selection
public class StyleableTextFieldView<P extends ContentEditable.ContentPoint>
        extends StyleableView<InputField<?, P>, StyleableTextFieldView.TextFieldStyle> {

    private final TextFieldContent textFieldContent = new TextFieldContent();
    private final ColorFill caret = new ColorFill().color(Colors.BLACK);

    public StyleableTextFieldView(InputField<?, P> model) {
        super(model);
        //cursor.set(Cursor.TEXT);
    }

    @Override
    protected Element contentImpl() {
        return textFieldContent;
    }

    // cursor(Cursor.TEXT);
    @EventHandler
    private Event.ReleaseCallback beginPress(Event.BeginPress beginPress) {
        @SuppressWarnings("unchecked")
        P newPos = (P) model.content.pickPosition(beginPress.point());

        model.caretPosition.set(newPos);
        model.content.select(null);

        return new Event.ReleaseCallback() {

            private P selectionBegin = newPos;

            @Override
            public void drag(Point point) {
                @SuppressWarnings("unchecked")
                P p = (P) model.content.pickPosition(point);

                model.caretPosition.set(p);

                P begin = ContentEditable.ContentPoint.min(selectionBegin, model.caretPosition.get());
                P end = ContentEditable.ContentPoint.max(selectionBegin, model.caretPosition.get());
                model.content.select(begin == end ? null : new ContentEditable.ContentRange<>(begin, end));
            }

            @Override
            public void commit() {
            }

            @Override
            public void cancel() {
            }
        };
    }

    @EventHandler
    private Event.AcceptFocus focus(Event.Focus focusEvent) {
        return new Event.AcceptFocus(()->{});
    }

    @EventHandler
    private void enterContent(Event.EnterContent enterContent) throws IOException, UnsupportedFlavorException {
        model.typeText((String) enterContent.transferable().getTransferData(DataFlavor.stringFlavor));
    }

    @EventHandler
    private void functionKey(Event.KeyCombinationEvent keyCombinationAction) {
        if (keyCombinationAction.keyCombination().keySymbol() instanceof StandardFunctionSymbol sym)
            switch (sym) {
                case LEFT -> model.caretLeft();
                case RIGHT -> model.caretRight();
                case BACKSPACE -> model.backspace();
                case DELETE -> model.delete();
            }
    }

    public interface TextFieldStyle extends Style {

        TextStyle textStyle();
    }

    private class TextFieldContent extends RectangularLayout {

        @Override
        public void enumerateChildren(Consumer<Element> consumer) {
            consumer.accept(model.content);
            consumer.accept(caret);
        }

        @Override
        public Size preferredSize(BoxConstraints constraints, LayoutContext1 context) {
            Size caretWidth = new Size(1, 0);
            return context.preferredSize(model.content, constraints.subtract(caretWidth)).add(caretWidth);
        }

        @Override
        protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
            placer.accept(model.content, Rectangle.of(size));
            Shape rangeShape = model.content.shapeOfSelection(
                            new ContentEditable.ContentRange<>(model.caretPosition.get(), model.caretPosition.get())).
                    translate(origin().negate());
            Rectangle rect = rangeShape.bounds();
            placer.accept(caret, rect.withSize(new Size(1, rect.height())));
        }

    }
}
