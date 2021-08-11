package ui10.layout4;

import ui10.geom.Insets;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

import java.util.List;
import java.util.function.Function;

public class Layouts {

    public static record Stack(List<LayoutNode> children) implements LayoutNode {

        @Override
        public Size computeSize(LayoutContext context, BoxConstraints constraints) {
            Size size = Size.ZERO;
            for (LayoutNode n : children)
                size = Size.max(size, n.computeSize(context, constraints));
            return size;
        }

        @Override
        public void setBounds(LayoutContext context, Rectangle bounds) {
            for (LayoutNode child : children)
                child.setBounds(context, bounds);
        }
    }

    public static record Padding(LayoutNode content,
                                 Function<LayoutContext, Insets> insetsFunction) implements LayoutNode {

        @Override
        public Size computeSize(LayoutContext context, BoxConstraints constraints) {
            Insets i = insetsFunction.apply(context);
            Size s = content.computeSize(context, constraints.subtract(new Point(i.horizontal(), i.vertical())));
            return new Size(s.width() + i.horizontal(), s.height() + i.vertical());
        }

        @Override
        public void setBounds(LayoutContext context, Rectangle bounds) {
            Insets i = insetsFunction.apply(context);
            content.setBounds(context, bounds.withInsets(i));
        }
    }

    public static record Centered(LayoutNode content) implements LayoutNode {
        @Override
        public Size computeSize(LayoutContext context, BoxConstraints constraints) {
            if (constraints.isFixed())
                return constraints.min();
            return constraints.clamp(content.computeSize(context, constraints.withMinimum(Size.ZERO)));
        }

        @Override
        public void setBounds(LayoutContext context, Rectangle bounds) {
            Size contentSize = content.computeSize(context, BoxConstraints.fixed(bounds.size()));
            content.setBounds(context, bounds.centered(contentSize));
        }
    }


//
//        @Override
//        public LayoutNode.SizeAndAttachment computeSize2(BoxConstraints constraints, Object attachment) {
//            LayoutNode[] n = (LayoutNode[]) attachment;
//            Object[] c = new Object[n.length];
//            Size size = Size.ZERO;
//            int validFrom = 0;
//            for (int i = 0; i < n.length; i++) {
//                LayoutNode.SizeAndAttachment s = n[i].computeSize2(constraints);
//                c[i] = s.attachment();
//
//                if (s.size().equals(constraints.max())) {
//                    for (int j = 0; j < i; j++)
//                        c[i] = n[i].computeSize2(constraints);
//                    for (int j = i+1; j < n.length; j++)
//                        c[i] = n[i].computeSize2(constraints);
//                    return new LayoutNode.SizeAndAttachment(size, c);
//                }
//
//                if (s.size().width() > size.width() || s.size().height() > size.height()) {
//                    size = Size.max(size, s.size());
//                    if (s.size().equals(size))
//                        validFrom = i;
//                    else
//                        validFrom = i + 1;
//                }
//            }
//            for (int i = 0; i <validFrom; i++)
//                c[i] = n[i].computeSize2(constraints).attachment();
//            return new LayoutNode.SizeAndAttachment(size, c);
//        }

}
