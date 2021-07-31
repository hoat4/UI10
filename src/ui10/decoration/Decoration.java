package ui10.decoration;

import ui10.binding.PropertyHolder;
import ui10.binding.PropertyTransformation;
import ui10.binding.ScalarProperty;
import ui10.nodes2.Pane;

public abstract class Decoration extends PropertyHolder implements PropertyTransformation<Pane> {

    private boolean valid;

    public ScalarProperty<Boolean> valid() {
        return property((Decoration d) -> d.valid, (d, v) -> d.valid = v);
    }

    protected void invalidate() {
        valid().set(false);
    }


}
