package ui10.input;

import ui10.geom.Point;
import ui10.input.keyboard.KeyCombination;

import java.awt.datatransfer.Transferable;

public interface Event<R extends Event.EventResponse> {

    record Focus() implements Event<AcceptFocus> {
    }

    /*
    record BeginHover() implements Event {
    }

    record EndHover() implements Event {
    }
    */

    record BeginPress(Point point) implements Event<ReleaseCallback> {
        // ez a Point lehet hogy nem kéne ide
    }

    /*
    record ShowContextMenu(Point origin) implements Event {
    }

    record DoubleClick() implements Event {
    }

    record Scroll(int deltaX, int deltaY) implements Event {
    }

    record WheelClick() implements Event {
    }

    record Drag(DragHandler dragHandler) implements Event {
    }

    record DragEnter(DropHandler dropHandler) implements Event {
    }
     */

    record EnterContent(Transferable transferable) implements Event<OKResult> {
    }

    record Copy() implements Event<CopyResult> {
    }

    record Cut() implements Event<CopyResult> {
    }

    record KeyCombinationEvent(KeyCombination keyCombination) implements Event<OKResult> {
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
