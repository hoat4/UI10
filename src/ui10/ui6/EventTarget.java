package ui10.ui6;

import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.util.function.Consumer;

public class EventTarget implements Element {

    private final Element content;
    private final EventHandler eventHandler;

    public EventTarget(Element content, EventHandler eventHandler) {
        this.content = content;
        this.eventHandler = eventHandler;
    }

    @Override
    public Shape computeShape(BoxConstraints constraints) {
        return content.computeShape(constraints);
    }

    @Override
    public void applyShape(Shape shape, Consumer<Surface> consumer) {
        //  laposítani kéne
        Pane pane = new Pane() {
            {
                eventHandler = EventTarget.this.eventHandler;
            }

            @Override
            public Element content() {
                return content;
            }
        };

        pane.applyShape(shape, consumer);
    }
}
