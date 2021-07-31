package ui10.nodes2;

import ui10.binding.*;
import ui10.geom.Rectangle;
import ui10.layout.BoxConstraints;

import java.util.function.Consumer;

public class FrameImpl extends PropertyHolder implements Frame {

    private final ScalarProperty<Pane> pane = ScalarProperty.create();

    private Rectangle bounds;
    private FrameAndLayout appliedLayout;
    private boolean layoutInvalid;

    {
        appliedLayout().subscribe(e -> {
            if (e.oldValue() != null)
                e.oldValue().paneLayout().valid().unsubscribe(new LayoutInvalidationSubscriber(this));
            e.newValue().paneLayout().valid().subscribe(new LayoutInvalidationSubscriber(this));
        });
    }

    public FrameImpl(Pane pane) {
        this.pane.set(pane);
    }

    @Override
    public ScalarProperty<Pane> pane() {
        return pane;
    }

    public FrameAndLayout layout(BoxConstraints constraints) {
        Pane.Layout l = pane.get().computeLayout(constraints);
        return new FrameAndLayout(this, constraints, l, l.size());
    }

    public ScalarProperty<FrameAndLayout> appliedLayout() {
        return property((FrameImpl f) -> f.appliedLayout, (f, v) -> f.appliedLayout = v);
    }

    @Override
    public ScalarProperty<Rectangle> bounds() {
        return property((FrameImpl f) -> f.bounds, (f, v) -> f.bounds = v);
    }

    public ScalarProperty<Boolean> layoutInvalid() {
        return property((FrameImpl f) -> f.layoutInvalid, (f, v) -> f.layoutInvalid = v);
    }

    private record LayoutInvalidationSubscriber(FrameImpl frame) implements Consumer<ChangeEvent<Boolean>> {

        @Override
        public void accept(ChangeEvent<Boolean> e) {
            if (e.oldValue() && !e.newValue()) {
                FrameAndLayout newLayout = frame.layout(frame.appliedLayout.inputConstraints());
                if (newLayout.size().equals(frame.appliedLayout.size()))
                    frame.appliedLayout().set(newLayout);
                else
                    frame.layoutInvalid().set(false);
            }
        }
    }

    @Override
    public String toString() {
        return pane.get() + " @ " + bounds;
    }
}
