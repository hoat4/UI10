package ui10.pane;

import ui10.binding.ObservableList;
import ui10.binding.ScalarProperty;
import ui10.geom.Size;
import ui10.input.InputEnvironment;
import ui10.layout.BoxConstraints;

import java.util.Map;

public interface Pane {

    Layout computeLayout(BoxConstraints constraints);

    // null, ha primitive
    ObservableList<? extends FrameImpl> children();

    ScalarProperty<Frame> frame();

    ScalarProperty<InputEnvironment> inputEnvironment();

    Map<Object, Object> extendedProperties();

    interface Layout {

        Size size();

        // ezt úgy kéne csinálni, hogy ne lehessen null az értéke, mert így a renderer crashelhet
        ScalarProperty<Boolean> valid();

        void apply();
    }
}
