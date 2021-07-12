package ui10.node;

import ui10.binding.ObservableList;
import ui10.binding.ObservableScalar;
import ui10.binding.PropertyHolder;
import ui10.binding.ScalarProperty;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

public abstract class Node extends PropertyHolder {

    private Node parent;

    private Point position;
    private Size size;

    public Object rendererData;

    public abstract ObservableList<Node> children();

    public abstract Layout layout(BoxConstraints constraints);

    public NodeType nodeType() {
        return new ClassNodeType(getClass());
    }

    public ObservableScalar<Point> position() {
        return property((Node n) -> n.position, (n, v) -> n.position = v);
    }

    public ObservableScalar<Size> size() {
        return sizeProp();
    }

    private ScalarProperty<Size> sizeProp() {
        return property((Node n) -> n.size, (n, v) -> n.size = v);
    }

    public class Layout {

        public final Size size;
        private final Runnable applier;

        public Layout(Size size, Runnable applier) {
            this.size = size;
            this.applier = applier;
        }

        public void apply() {
            sizeProp().set(size);
            applier.run();
        }
    }
}
