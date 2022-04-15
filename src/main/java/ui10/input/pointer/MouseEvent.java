package ui10.input.pointer;

import ui10.geom.Point;
import ui10.input.InputEvent;

public interface MouseEvent extends InputEvent {

    Point point();

    MouseEvent subtract(Point offset);

    interface MouseButtonEvent extends MouseEvent {
    }

    record MousePressEvent(Point point, MouseButton button/*, Set<MouseButton> pressedButtons?*/) implements MouseButtonEvent {
        @Override
        public MouseEvent subtract(Point offset) {
            return new MousePressEvent(point.subtract(offset), button);
        }
    }

    record MouseDragEvent(Point point) implements MouseButtonEvent {
        @Override
        public MouseEvent subtract(Point offset) {
            return new MouseDragEvent(point.subtract(offset));
        }
    }

    record MouseMoveEvent(Point point) implements MouseButtonEvent {
        @Override
        public MouseEvent subtract(Point offset) {
            return new MouseMoveEvent(point.subtract(offset));
        }
    }

    record MouseReleaseEvent(Point point, MouseButton button) implements MouseButtonEvent {
        @Override
        public MouseEvent subtract(Point offset) {
            return new MouseReleaseEvent(point.subtract(offset), button);
        }
    }

    enum MouseButton {
        LEFT_BUTTON, WHEEL, RIGHT_BUTTON // legyen inkább BUTTON1, stb.?
    }
}
