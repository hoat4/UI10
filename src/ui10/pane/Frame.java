package ui10.pane;

import ui10.binding.ScalarProperty;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

public interface Frame {

    // Pane container(); ezt nehéz megcsinálni, mert Item konstruktorából nem tudjuk hogy mi legyen a container

    ScalarProperty<Pane> pane();

    ScalarProperty<Rectangle> bounds();

    FrameAndLayout layout(BoxConstraints constraints);


    public record FrameAndLayout(FrameImpl frame, BoxConstraints inputConstraints,
                                 Pane.Layout paneLayout, Size size) {
    }


}
