package ui10.controls;

import ui10.base.*;
import ui10.binding.ScalarProperty;
import ui10.decoration.DecorationContext;
import ui10.decoration.css.CSSProperty;
import ui10.decoration.css.Styleable;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.image.Colors;
import ui10.input.InputEvent;
import ui10.input.keyboard.KeyTypeEvent;
import ui10.input.keyboard.Keyboard;
import ui10.input.pointer.MouseEvent;
import ui10.layout.BoxConstraints;
import ui10.decoration.css.CSSClass;
import ui10.graphics.ColorFill;
import ui10.graphics.TextNode;
import ui10.layout.RectangularLayout;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static ui10.decoration.css.CSSClass.withClass;
import static ui10.input.keyboard.Keyboard.StandardFunctionSymbol.*;

public class TextField extends Control implements Styleable {

    public final ScalarProperty<String> text = ScalarProperty.createWithDefault("TextField.text", "szöveg");
    public final ScalarProperty<Integer> caretPosition = ScalarProperty.createWithDefault("TextField.caretPosition", 0);

    private final TextNode textNode = withClass("text", new TextNode());
    private final ColorFill caret = withClass("caret", new ColorFill().color(Colors.BLACK));

    {
        attributes().add(new CSSClass("text-field"));
        text.subscribe(e -> invalidatePane());
        caretPosition.subscribe(e -> invalidatePane());
    }

    @Override
    protected void validate() {
        super.validate();
        textNode.text(text.get());
    }

    @Override
    public Element content() {
        return new RectangularLayout() {
            @Override
            public void enumerateStaticChildren(Consumer<Element> consumer) {
                consumer.accept(textNode);
                consumer.accept(caret);
            }

            @Override
            public Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
                return context.preferredSize(textNode, constraints);
            }

            @Override
            protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
                placer.accept(textNode, Rectangle.of(size));

                int caretX = textNode.textStyle().textSize(text.get().substring(0, caretPosition.get())).width();

                Rectangle caretShape = new Rectangle(caretX, 0, 1, size.height());
                placer.accept(caret, caretShape);
            }

        };
    }

    @EventHandler
    private void onMousePress(MouseEvent.MousePressEvent event, EventContext context) {
        focusContext.focusedControl.set(this);
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
        if (caretPosition.get() > 0)
            caretPosition.set(caretPosition.get() - 1);
    }

    @OnFunctionKey(RIGHT)
    public void right() {
        if (caretPosition.get() < text.get().length())
            caretPosition.set(caretPosition.get() + 1);
    }

    @OnFunctionKey(BACKSPACE)
    public void backspace() {
        if (caretPosition.get() > 0) {
            text.set(text.get().substring(0, caretPosition.get() - 1) + text.get().substring(caretPosition.get()));
            caretPosition.set(caretPosition.get()-1);
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
}
