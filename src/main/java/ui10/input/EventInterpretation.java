package ui10.input;

import ui10.base.Element;
import ui10.binding5.ElementEvent;
import ui10.controls.Button;
import ui10.geom.Point;
import ui10.input.keyboard.KeyCombination;

import java.awt.datatransfer.Transferable;

public interface EventInterpretation<R extends EventInterpretation.EventResponse> {

    EventTarget target();

    record Focus(EventTarget target) implements EventInterpretation<AcceptFocus> {
    }

    /*
    record BeginHover() implements EventInterpretation {
    }

    record EndHover() implements EventInterpretation {
    }
    */

    interface BeginPress extends EventInterpretation<ReleaseCallback> {
    }

    interface BeginSemanticPress extends BeginPress {
    }

    record BeginButtonRolePress(Button.Role buttonRole) implements BeginSemanticPress {

        @Override
        public EventTarget target() {
            return new EventTarget.ButtonRoleTarget(buttonRole);
        }
    }

    record BeginMousePress(Point point) implements BeginPress {
        @Override
        public EventTarget target() {
            return new EventTarget.PointTarget(point);
        }
    }

    /*
    record ShowContextMenu(Point origin) implements EventInterpretation {
    }

    record DoubleClick() implements EventInterpretation {
    }

    record Scroll(int deltaX, int deltaY) implements EventInterpretation {
    }

    record WheelClick() implements EventInterpretation {
    }

    record Drag(DragHandler dragHandler) implements EventInterpretation {
    }

    record DragEnter(DropHandler dropHandler) implements EventInterpretation {
    }
     */

    record EnterContent(EventTarget target, Transferable transferable) implements EventInterpretation<OKResult> {
    }

    record Copy(EventTarget target) implements EventInterpretation<CopyResult>{
    }

    record Cut(EventTarget target) implements EventInterpretation<CopyResult> {
    }

    record KeyCombinationEvent(EventTarget target, KeyCombination keyCombination) implements EventInterpretation<OKResult> {
    }

    interface DragHandler {

        void beginDrag(DragCallback callback);

        void content(Transferable content);

        interface DragCallback {

            void moveTo(Point point);

            void cancel();

            void commit();
        }
    }

    interface DropHandler {

        Transferable content();

        void accept(DropCallback callback);

        void deny();

        interface DropCallback {

            void commitDrop();
        }
    }

    interface EventResponse {

    }

    record CopyResult(Transferable content) implements EventResponse {
    }

    interface ReleaseCallback extends EventResponse {

        default void drag(Point point) {
        }

        void commit();

        void cancel();
    }

    record OKResult() implements EventResponse {
    }

    record AcceptFocus(FocusLostListener focusLostListener) implements EventResponse {
    }

    @FunctionalInterface
    interface FocusLostListener {
        void focusLost();
    }
}
