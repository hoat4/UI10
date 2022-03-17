package ui10.base;

import ui10.geom.Size;
import ui10.layout.BoxConstraints;

import java.util.Objects;

// inputot nevezzük inkább Constraintsnek? Flutterben is úgy hívják
public interface LayoutProtocol<I, O> {

    O preferredSize(Element element, I constraints, LayoutContext1 context);

    LayoutProtocol<BoxConstraints, Size> BOX = (e, constraints, context)->{
        Size s = e.preferredSizeImpl(constraints, context);
        Objects.requireNonNull(s, e::toString);

        if (s.isInfinite())
            throw new IllegalStateException("preferred size must be finite: " + s + " (by " + e + ")"); // milyen exceptionnek kéne ennek lennie?
        if (!constraints.contains(s))
            throw new IllegalStateException("invalid size returned by preferredSizeImpl for " +
                    constraints + ": " + s + " (by " + e + ")");
        return s;
    };
}
