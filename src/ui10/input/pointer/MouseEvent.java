package ui10.input.pointer;

import ui10.geom.Point;

public interface MouseEvent {

    Point point();

    interface MouseButtonEvent extends MouseEvent {
    }

    record MousePressEvent(Point point, MouseButton button/*, Set<MouseButton> pressedButtons?*/) implements MouseButtonEvent {
    }

    record MouseReleaseEvent(Point point) implements MouseButtonEvent {
    }

    enum MouseButton {
        LEFT_BUTTON, WHEEL, RIGHT_BUTTON // legyen inkább BUTTON1, stb.?
    }
}
