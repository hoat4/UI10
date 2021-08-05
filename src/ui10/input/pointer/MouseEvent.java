package ui10.input.pointer;

import ui10.geom.Point;
import ui10.input.InputEvent;

public interface MouseEvent extends InputEvent {

    Point point();

    interface MouseButtonEvent extends MouseEvent {
    }

    record MousePressEvent(Point point, MouseButton button/*, Set<MouseButton> pressedButtons?*/) implements MouseButtonEvent {
    }

    record MouseReleaseEvent(Point point, MouseButton button) implements MouseButtonEvent {
    }

    enum MouseButton {
        LEFT_BUTTON, WHEEL, RIGHT_BUTTON // legyen inkább BUTTON1, stb.?
    }
}
