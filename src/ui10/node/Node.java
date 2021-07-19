package ui10.node;

import ui10.binding.*;
import ui10.font.FontContext;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

import java.util.Objects;

public abstract class Node extends PropertyHolder {

    Node parent;
    EventLoop eventLoop;

    private Point position;
    private Size size;

    private Layout appliedLayout;

    private FontContext font;

    public Object rendererData;

    protected abstract ObservableList<Node> createChildren();

    private ObservableList<Node> children;

    public ObservableList<Node> children() {
        if (children == null) {
            children = createChildren();
            if (children != null) {
                if (eventLoop == null) {
                    eventLoop().subscribe(c -> {
                        for (Node n : children)
                            n.eventLoop().set(eventLoop);
                    });
                }

                children.enumerateAndSubscribe(ObservableList.simpleListSubscriber(added -> {
                    if (added.parent != null)
                        throw new IllegalStateException("node already added to another parent: " + added);
                    added.parentProp().set(this);
                    added.eventLoop().set(eventLoop);
                }, removed -> {
                    if (removed.parent == null)
                        throw new IllegalStateException("node already removed: " + removed);
                    removed.parentProp().set(null);
                    removed.eventLoop().set(null);
                }));

            }
        }
        return children;
    }

    private Layout prevLayout;

    public Layout layout(BoxConstraints constraints) {
        Objects.requireNonNull(constraints);
        if (prevLayout != null && prevLayout.valid.get() &&
                prevLayout.inputConstraints.equals(constraints))
            return prevLayout;

        children();

        return prevLayout = computeLayout(constraints);
    }

    protected abstract Layout computeLayout(BoxConstraints constraints);

    public NodeType nodeType() {
        return new ClassNodeType(getClass());
    }

    public ObservableScalar<Point> position() {
        return positionProp();
    }

    private ScalarProperty<Point> positionProp() {
        return property((Node n) -> n.position, (n, v) -> n.position = v);
    }

    public ObservableScalar<Size> size() {
        return sizeProp();
    }

    private ScalarProperty<Size> sizeProp() {
        return property((Node n) -> n.size, (n, v) -> n.size = v);
    }

    public ObservableScalar<Node> parent() {
        return parentProp();
    }

    private ScalarProperty<Node> parentProp() {
        return property((Node n) -> n.parent, (n, v) -> n.parent = v);
    }

    public ScalarProperty<EventLoop> eventLoop() {
        return property((Node n) -> n.eventLoop, (n, v) -> n.eventLoop = v);
    }

    public ObservableScalar<Layout> appliedLayout() {
        return appliedLayoutProp();
    }

    private ScalarProperty<Layout> appliedLayoutProp() {
        return property((Node n) -> n.appliedLayout, (n, v) -> n.appliedLayout = v);
    }

    public ScalarProperty<FontContext> font() {
        return inheritableProperty(Node::parent, (Node n) -> n.font, (n, v) -> n.font = v);
    }

    public abstract class Layout {

        public final Size size;
        public final ScalarProperty<Boolean> valid = ScalarProperty.<Boolean>create().set(false);
        public final BoxConstraints inputConstraints;

        public Layout(BoxConstraints inputConstraints, Size size) {
            this.inputConstraints = inputConstraints;
            Objects.requireNonNull(size);
            this.size = inputConstraints.clamp(size);

            if (children() != null)
                children().subscribe(c -> valid.set(false));
        }

        public Node node() {
            return Node.this;
        }

        public final void apply(Point point) {
            positionProp().set(point);
            sizeProp().set(size);
            appliedLayoutProp().set(this);
            apply();
        }

        protected abstract void apply();

        protected void applyChild(Layout childLayout, Point childPos) {
            childLayout.apply(childPos);
            childLayout.valid.subscribe(c -> {
                if (!c.newValue() && c.oldValue()) {
                    Layout newLayout = childLayout.node().layout(childLayout.inputConstraints);
                    if (newLayout.size.equals(childLayout.size))
                        applyChild(newLayout, childPos);
                    else
                        valid.set(false);
                }
            });
        }
    }
}
