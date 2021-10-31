package ui10.controls;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.decoration.Tag;
import ui10.font.TextStyle;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.image.Colors;
import ui10.input.InputEvent;
import ui10.input.keyboard.KeyTypeEvent;
import ui10.input.keyboard.Keyboard;
import ui10.input.pointer.MouseEvent;
import ui10.layout.AbsolutePositioned;
import ui10.layout.FixedSize;
import ui10.layout.StackPane;
import ui10.nodes.LinePane;
import ui10.nodes.Node;
import ui10.nodes.Pane;
import ui10.nodes.TextPane;

import static ui10.binding.ObservableScalar.binding;



public class TextField extends Control {

    public static final Tag TAG = new Tag("TextField");
    public static final Tag TEXT_TAG = new Tag("TextFieldText");

    public final ScalarProperty<String> text = ScalarProperty.create();
    public final ScalarProperty<TextStyle> textStyle = ScalarProperty.create();

    private final ScalarProperty<Integer> caretPosition = ScalarProperty.createWithDefault(0);

    {
        tags().add(TAG);
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        TextPane textPane = new TextPane(textStyle, text);
        textPane.tags().add(TEXT_TAG);

        ObservableScalar<Point> caretPos = binding(caretPosition, textStyle, text, (pos, textStyle, text) ->
                new Point(textStyle == null ? 0 : textStyle.textSize(text.substring(0, pos)).width(), 0));

        return ObservableScalar.ofConstant(new StackPane(
                textPane,
                new AbsolutePositioned(ObservableScalar.ofConstant(new Caret()), caretPos)
        ));
    }

    protected void handleEvent(InputEvent e) {
        if (e instanceof MouseEvent.MousePressEvent) {
            context.get().inputEnvironment.focus().set(eventTarget);
        } else if (e instanceof KeyTypeEvent k) {
            k.symbol().standardSymbol().ifPresent(sym -> {
                if (sym instanceof Keyboard.StandardTextSymbol textSymbol) {
                    String s = text.get();
                    text.set(s.substring(0, caretPosition.get()) + textSymbol.text() + s.substring(caretPosition.get()));
                    caretPosition.set(caretPosition.get() + 1);
                } else if (sym instanceof Keyboard.StandardFunctionSymbol functionSymbol) {
                    switch (functionSymbol) {
                        case LEFT -> {
                            if (caretPosition.get() > 0)
                                caretPosition.set(caretPosition.get() - 1);
                        }
                        case RIGHT->{
                            if (caretPosition.get() < text.get().length())
                                caretPosition.set(caretPosition.get() + 1);
                        }
                    }
                }

            });
        }
    }

    public class Caret extends Pane {

        @Override
        protected ObservableScalar<? extends Node> paneContent() {
            LinePane line = new LinePane(1000, Colors.BLACK); // TODO

            FixedSize fs = new FixedSize();
            fs.content.set(line);
            fs.size.bindTo(textStyle.map(ts -> new Size(line.width.get(), ts == null ? 0 : ts.height())));

            return ObservableScalar.ofConstant(fs);
        }
    }
}
