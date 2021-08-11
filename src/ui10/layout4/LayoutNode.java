package ui10.layout4;

import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

import java.util.Objects;

public interface LayoutNode {

        Size computeSize(LayoutContext context, BoxConstraints constraints);

        void setBounds(LayoutContext context, Rectangle bounds);
}
