package ui10.decoration.views;

import ui10.base.*;
import ui10.binding7.PropertyBasedView;
import ui10.controls.InputField;
import ui10.controls.InputField.InputFieldProperty;
import ui10.decoration.Style;
import ui10.font.TextStyle;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.graphics.ColorFill;
import ui10.image.Colors;
import ui10.input.keyboard.KeyTypeEvent;
import ui10.input.keyboard.Keyboard;
import ui10.input.pointer.MouseEvent;
import ui10.layout.BoxConstraints;
import ui10.layout.RectangularLayout;
import ui10.window.Cursor;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

// .text-field, .text, .caret, .selection
public class StyleableTextFieldView<P extends ContentEditable.ContentPoint>
        extends PropertyBasedView<InputField<?, P>, StyleableTextFieldView.TextFieldStyle>
        implements InputHandler {

    private final TextFieldContent textFieldContent = new TextFieldContent();
    private final ColorFill caret = new ColorFill().color(Colors.BLACK);

    public StyleableTextFieldView(InputField<?, P> model) {
        super(model);
        //cursor.set(Cursor.TEXT);
    }

    @Override
    protected void validateImpl() {
        if (model.dirtyProperties().contains(InputFieldProperty.CARET_POSITION))
            textFieldContent.refresh();
    }

    @Override
    protected void onDecorationChanged() {
    }

    @Override
    protected Element contentImpl() {
        return new TextFieldMouseTarget(textFieldContent);
    }

    private class TextFieldMouseTarget extends MouseTarget {

        private P selectionBegin;

        {
            cursor(Cursor.TEXT);
        }

        public TextFieldMouseTarget(Element content) {
            super(content);
        }

        @Override
        public DragHandler handlePress(MouseEvent.MousePressEvent event) {
            focusContext().focusedControl.set(this);

            @SuppressWarnings("unchecked")
            P newPos = (P) model.content.pickPosition(event.point());

            model.caretPosition(newPos);
            model.content.select(null);

            selectionBegin = newPos;

            return new DragHandler() {

                @Override
                public void drag(MouseEvent.MouseDragEvent event) {
                    @SuppressWarnings("unchecked")
                    P p = (P) model.content.pickPosition(event.point());

                    model.caretPosition(p);

                    P begin = ContentEditable.ContentPoint.min(selectionBegin, model.caretPosition());
                    P end = ContentEditable.ContentPoint.max(selectionBegin, model.caretPosition());
                    model.content.select(begin == end ? null : new ContentEditable.ContentRange<>(begin, end));
                }

                @Override
                public void release(MouseEvent.MouseReleaseEvent event) {
                }
            };
        }
    }

    private class TextFieldKeyTarget extends KeyTarget {

        @Override
        public void onKeyType(KeyTypeEvent event) {
            switch (event.symbol()) {
                case Keyboard.StandardTextSymbol textSymbol -> model.typeText(textSymbol.text());
                case Keyboard.StandardFunctionSymbol functionSymbol -> {
                    switch (functionSymbol) {
                        case LEFT -> model.caretLeft();
                        case RIGHT -> model.caretRight();
                        case BACKSPACE -> model.backspace();
                        case DELETE -> model.delete();
                    }
                }
                default -> {
                }
            }
        }
    }

    public interface TextFieldStyle extends Style {

        TextStyle textStyle();
    }

    private class TextFieldContent extends RectangularLayout {

        void refresh() {
            listener().layoutInvalidated();
        }

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
                    new ContentEditable.ContentRange<>(model.caretPosition(), model.caretPosition())).
                    translate(origin().negate());
            Rectangle rect = rangeShape.bounds();
            placer.accept(caret, rect.withSize(new Size(1, rect.height())));
        }

    }
}
