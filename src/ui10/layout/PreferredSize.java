package ui10.layout;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.nodes.Node;
import ui10.nodes.WrapperPane;

public class PreferredSize extends WrapperPane {

    public final ScalarProperty<Size> size = ScalarProperty.create();

    public PreferredSize() {
    }

    public PreferredSize(Node content, Size size) {
        super(content);
        this.size.set(size);
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        return ObservableScalar.ofConstant(new OneChildOnePassLayout(content) {
            @Override
            protected BoxConstraints childConstraints(BoxConstraints thisConstraints) {
                return BoxConstraints.fixed(thisConstraints.clamp(PreferredSize.this.size.get()));
            }

            @Override
            protected Size layout(BoxConstraints constraints, Node content, Size contentSize, boolean apply) {
                if (apply)
                    content.position.set(Point.ORIGO);
                return contentSize;
            }
        });
    }
}
