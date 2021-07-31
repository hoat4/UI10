package ui10.nodes2;

import ui10.binding.ScalarProperty;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

public interface Frame {

    ScalarProperty<Pane> pane();

    ScalarProperty<Rectangle> bounds();

    FrameAndLayout layout(BoxConstraints constraints);

    public record FrameAndLayout(FrameImpl frame, BoxConstraints inputConstraints,
                                 Pane.Layout paneLayout, Size size) {
    }


}
