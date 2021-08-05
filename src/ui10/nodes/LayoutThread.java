package ui10.nodes;

import ui10.binding.ScalarProperty;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

public class  LayoutThread {

    public final ScalarProperty<BoxConstraints> constraints = ScalarProperty.create();
    public final ScalarProperty<Size> size = ScalarProperty.create();
}
