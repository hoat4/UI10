package ui10.layout;

import ui10.binding.*;
import ui10.geom.Size;
import ui10.nodes.LayoutNode;
import ui10.nodes.LayoutThread;
import ui10.nodes.Node;
import ui10.nodes.Pane;

import java.util.ArrayList;
import java.util.List;

import static ui10.geom.Point.ORIGO;

public class StackPane extends Pane {

    public final ObservableList<Node> children;

    public StackPane() {
        this.children = new ObservableListImpl<>();
    }

    public StackPane(Node... children) {
        this.children = ObservableListImpl.createMutable(children);
    }

    public StackPane(List<Node> children) {
        this.children = ObservableListImpl.createMutable(children);
    }

    public StackPane(ObservableList<Node> children) {
        this.children = children;
    }

    @Override
    protected ObservableScalar<Node> paneContent() {
        return ObservableScalar.ofConstant(new LayoutNode(children) {
            @Override
            protected ObservableScalar<Size> makeLayoutThread(ObservableScalar<BoxConstraints> in,
                                                              boolean apply, Scope scope) {
                ObservableScalar<Size> thisSize = children.streamBinding().
                        flatMapProp(n -> n.layoutThread(in, false, null)).
                        reduce(in.map(BoxConstraints::min), Size::max);

                if (apply) {
                    ObservableScalar<BoxConstraints> childConstraints = thisSize.map(BoxConstraints::fixed);

                    // TODO original scope childje legyen
                    children.scopedEnumerateAndSubscribe((n, scope2) -> {
                        n.position.set(ORIGO);
                        n.layoutThread(childConstraints, true, scope2);
                    });
                }

                return thisSize;
            }
        });
    }

//        return ObservableScalar.ofConstant(new LayoutNode(children) {
//
//            @Override
//            public LayoutResult doLayout(BoxConstraints constraints) {
//                Size size = children.stream().
//                        map(n -> childLayout(n, constraints).size()).
//                        reduce(constraints.min(), Size::max);
//
//                return new LayoutResult(size, __ -> {
//                    for (Node p : children)
//                        childLayout(p, BoxConstraints.fixed(size)).apply(ORIGO);
//                });
//            }
//        });
}
