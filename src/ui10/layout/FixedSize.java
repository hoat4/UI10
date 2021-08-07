package ui10.layout;

import ui10.binding.ObservableList;
import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.geom.Num;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.nodes.LayoutNode;
import ui10.nodes.Node;
import ui10.nodes.Pane;
import ui10.nodes.WrapperPane;

import java.util.Collection;

public class FixedWidth extends WrapperPane {
    public final ScalarProperty<Num> width = ScalarProperty.create();

    public FixedWidth() {
    }

    public FixedWidth(Node content, Num width) {
        super(content);
        this.width.set(width);
    }

    public FixedWidth(ObservableScalar<Node> content, ObservableScalar<Num> width) {
        super(content);
        this.width.bindTo(width);
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        return ObservableScalar.ofConstant(new LayoutNode(ObservableList.of(content)) {

            {
                layoutDependsOn(width);
            }

            @Override
            protected Size doDetermineSize(BoxConstraints constraints) {
                Num w = width.get();
                if (!constraints.containsWidth(w))
                    throw new IllegalStateException("fixed width doesn't fits in " + constraints + " (content: " + content.get() + ")");
                return content.get().determineSize(constraints.withWidth(w, w));
            }

            @Override
            public void layout(Collection<Node> updatedChildren) {
                content.get().bounds.set(Rectangle.of(bounds.get().size()));
            }
        });
    }
}
