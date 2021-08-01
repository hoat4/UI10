package ui10.nodes2;

import ui10.binding.ObservableList;
import ui10.binding.PropertyHolder;
import ui10.binding.ScalarProperty;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

import java.util.Map;

import static ui10.geom.Point.ORIGO;

public abstract class AbstractPane extends PropertyHolder implements Pane {

    private boolean childrenCreated;
    private ObservableList<? extends FrameImpl> children;

    protected abstract ObservableList<? extends FrameImpl> makeChildList();

    @Override
    public abstract AbstractLayout computeLayout(BoxConstraints constraints);

    @Override
    public ObservableList<? extends FrameImpl> children() {
        if (!childrenCreated) {
            children = makeChildList();
            childrenCreated = true;
        }
        return children;
    }

    @Override
    public Map<Object, Object> extendedProperties() {
        return super.extendedProperties;
    }

    protected abstract static class AbstractLayout implements Layout {

        final BoxConstraints inputConstraints;
        /**
         * clamped to constraints
         */
        protected final Size size;
        private final ScalarProperty<Boolean> valid = ScalarProperty.<Boolean>create().set(false);

        public AbstractLayout(BoxConstraints inputConstraints, Size size) {
            this.inputConstraints = inputConstraints;
            this.size = inputConstraints.clamp(size);
        }

        @Override
        public final Size size() {
            return size;
        }

        @Override
        public ScalarProperty<Boolean> valid() {
            return valid;
        }

        protected void applyChild(Frame.FrameAndLayout childLayout, Point pos) {
            childLayout.paneLayout().apply();
            childLayout.frame().bounds().set(new Rectangle(pos, childLayout.size()));
            childLayout.frame().appliedLayout().set(childLayout);
            childLayout.frame().layoutInvalid().subscribe(e -> { // TODO unsubscribe
                if (!e.newValue())
                    valid.set(false);
            });
            // TODO unsubscribe?
            childLayout.paneLayout().valid().subscribe(c -> {
                if (!c.newValue() && c.oldValue()) {
                    Frame.FrameAndLayout newLayout = childLayout.frame().layout(childLayout.inputConstraints());
                    if (newLayout.size().equals(childLayout.size()))
                        applyChild(newLayout, pos);
                    else
                        invalidate();
                }
            });
        }

        protected void invalidate() {
            valid().set(false);
        }

        public static AbstractLayout wrap(Frame.FrameAndLayout l) {
            return new AbstractLayout(l.inputConstraints(), l.size()) {
                @Override
                public void apply() {
                    applyChild(l, ORIGO);
                }
            };
        }
    }
}
