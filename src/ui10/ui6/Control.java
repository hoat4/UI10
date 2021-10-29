package ui10.ui6;

import ui10.input.InputEvent;

public abstract class Control extends Decorable implements EventHandler {

    @Override
    protected Element wrapDecoratedInner(Element node) {
        return new Pane() {

            {
                eventHandler = Control.this;
            }

            @Override
            public Element content() {
                return node;
            }
        };
    }

    @Override
    public boolean capture(InputEvent event) {
        return false;
    }
}
