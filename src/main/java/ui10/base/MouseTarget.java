package ui10.base;

import ui10.binding7.PropertyBasedModel;
import ui10.input.pointer.MouseEvent;
import ui10.window.Cursor;

public abstract class MouseTarget extends PropertyBasedModel<MouseTarget.MouseHandlerProperty> {

    private Cursor cursor;

    public final Element content;

    public MouseTarget(Element content) {
        this.content = content;
    }

    public abstract DragHandler handlePress(MouseEvent.MousePressEvent event);

    protected void cursor(Cursor cursor) {
        if (cursor != this.cursor) {
            this.cursor = cursor;
            invalidate(MouseHandlerProperty.CURSOR);
        }
    }

    public Cursor cursor() {
        return cursor;
    }

    public enum MouseHandlerProperty {

        CURSOR
    }

    public interface DragHandler {

        void drag(MouseEvent.MouseDragEvent event);

        void release(MouseEvent.MouseReleaseEvent event);
    }
}
