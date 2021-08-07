package ui10.layout;

import ui10.binding.ObservableScalar;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.nodes.Layout;
import ui10.nodes.Node;
import ui10.nodes.WrapperPane;

import java.util.Collection;

public class Centered extends WrapperPane {

    public Centered() {
    }

    public Centered(Node content) {
        super(content);
    }

    public Centered(ObservableScalar<Node> content) {
        super(content);
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        return new Layout(content) {

            @Override
            protected Size determineSize(BoxConstraints constraints) {
                return constraints.clamp(content.get().determineSize(constraints.withMinimum(Size.ZERO)));
            }

            @Override
            public void layout(Collection<?> updatedChildren) {
                Node n = content.get();
                Size childSize = n.determineSize(new BoxConstraints(Size.ZERO, bounds.get().size()));
                n.bounds.set(Rectangle.of(bounds.get().size()).centered(childSize));
            }
        }.asNodeObservable();
    }

//    return ObservableScalar.ofConstant(new LayoutNode(ObservableList.of(content)) {
//
//        {
//            content.flatMapProp(c -> c.activeLayout).
//                    bindTo(activeLayout.flatMap(t -> ((LayoutThreadImpl) t).contentLayoutThread));
//
//            content.flatMapProp(c -> c.position).
//                    bindTo(activeLayout.flatMap(t -> ((LayoutThreadImpl)t).contentPosition()));
//        }
//
//        @Override
//        public LayoutThread makeLayoutThread() {
//            return new LayoutThreadImpl();
//        }
//
//        class LayoutThreadImpl extends LayoutThread {
//
//            final ObservableScalar<LayoutThread> contentLayoutThread = content.map(Node::makeLayoutThread);
//
//            {
//                contentLayoutThread.flatMapProp(t -> t.constraints).bindTo(constraints.map(c -> c.withMinimum(Size.ZERO)));
//                size.bindTo(constraints, contentLayoutThread.flatMap(t -> t.size), BoxConstraints::clamp);
//            }
//
//            ObservableScalar<Point> contentPosition() {
//                return binding(size, contentLayoutThread.flatMap(t -> t.size),
//                        (s, cs) -> Rectangle.of(s).centered(cs).topLeft());
//            }
//        }
//    });

//    @Override
//    public LayoutThread makeLayoutThread(boolean apply, Scope scope) {
//        LayoutThread contentThread = content.get().makeLayoutThread(apply, scope);
//        return constraints -> {
//            Size contentSize = contentThread.layout(constraints.withMinimum(Size.ZERO));
//            Size thisSize = constraints.clamp(contentSize);
//            if (apply)
//                content.get().position.set(Rectangle.of(thisSize).centered(contentSize).topLeft());
//            return thisSize;
//        };
//    }

//    @Override
//    public LayoutThread makeLayoutThread(boolean apply, Scope scope) {
//        ObservableScalar<LayoutThread> contentThread = content.map(c -> c.makeLayoutThread(apply, scope));
//        LayoutThread thisThread = new LayoutThread();
//        ObservableScalar<Size> contentSize = contentThread.flatMap(t -> t.size);
//
//        thisThread.size.bindTo(binding(thisThread.constraints, contentSize, BoxConstraints::clamp));
//        if (apply) {
//            ObservableScalar<Point> contentPos = binding(thisThread.size, contentSize,
//                    (s, cs) -> Rectangle.of(s).centered(cs).topLeft());
//            content.flatMapProp(n -> n.position).bindTo(contentPos);
//        }
//        return thisThread;
//    }
}
