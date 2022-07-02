package ui10.controls;

import ui10.base.Element;
import ui10.base.MouseTarget;
import ui10.input.pointer.MouseEvent;

import java.util.function.Consumer;

public class PressDetector extends MouseTarget {

    public final Element content;
    public final Consumer<ButtonState> stateChangeListener;

    public PressDetector(Element content, Consumer<ButtonState> stateChangeListener) {
        super(content);
        this.content = content;
        this.stateChangeListener = stateChangeListener;
    }

    @Override
    public DragHandler handlePress(MouseEvent.MousePressEvent event) {
        stateChangeListener.accept(new ButtonState(true, true, true));
        return new DragHandler() {

            @Override
            public void drag(MouseEvent.MouseDragEvent event) {
            }

            @Override
            public void release(MouseEvent.MouseReleaseEvent event) {
                stateChangeListener.accept(new ButtonState(true, true, false));
            }
        };
    }

    public record ButtonState(boolean hover, boolean focus, boolean press) {
    }
}
