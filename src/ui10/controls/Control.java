package ui10.controls;

import ui10.binding.ObservableScalar;
import ui10.input.EventTarget;
import ui10.input.InputEvent;
import ui10.input.InputEventHandler;
import ui10.nodes.Node;
import ui10.nodes.Pane;

public abstract class Control extends Pane {

    protected final EventTarget eventTarget = new EventTarget(InputEventHandler.of(this::handleEvent));
    public final ObservableScalar<Boolean> focused = context.
            flatMap(ctx->ctx.inputEnvironment.focus()).map(e -> e == eventTarget);

    @Override
    protected Node wrapDecoratedContent(Node decoratedContent) {
        eventTarget.content.set(decoratedContent);
        return eventTarget;
    }

    protected abstract void handleEvent(InputEvent e);

}
