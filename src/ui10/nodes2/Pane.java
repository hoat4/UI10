package ui10.nodes2;

import ui10.binding.ObservableList;
import ui10.binding.ScalarProperty;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

import java.util.Map;

public interface Pane {

    Layout computeLayout(BoxConstraints constraints);

    // null, ha primitive
    ObservableList<? extends FrameImpl> children();

    Map<Object, Object> extendedProperties();

    interface Layout {

        Size size();

        ScalarProperty<Boolean> valid();

        void apply();
    }
}
