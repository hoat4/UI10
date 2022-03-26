package ui10.decoration.css;

import ui10.controls.Label;
import ui10.decoration.BorderSpec;
import ui10.decoration.Fill;
import ui10.decoration.Interpolator;
import ui10.decoration.Interpolators;
import ui10.geom.Fraction;
import ui10.graphics.FontWeight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSSProperty<T> {

    private static final Map<String, CSSProperty<?>> properties = new HashMap<>();

    public static final CSSProperty<Fill> textColor = new CSSProperty<>("color", Interpolators.FILL);
    public static final CSSProperty<Length> fontSize = new CSSProperty<>("font-size", Interpolators.LENGTH);

    static final CSSProperty<Length> marginTop = new CSSProperty<>("margin-top", Interpolators.LENGTH);
    static final CSSProperty<Length> marginRight = new CSSProperty<>("margin-right", Interpolators.LENGTH);
    static final CSSProperty<Length> marginBottom = new CSSProperty<>("margin-bottom", Interpolators.LENGTH);
    static final CSSProperty<Length> marginLeft = new CSSProperty<>("margin-left", Interpolators.LENGTH);

    static final CSSProperty<Length> paddingTop = new CSSProperty<>("padding-top", Interpolators.LENGTH);
    static final CSSProperty<Length> paddingRight = new CSSProperty<>("padding-right", Interpolators.LENGTH);
    static final CSSProperty<Length> paddingBottom = new CSSProperty<>("padding-bottom", Interpolators.LENGTH);
    static final CSSProperty<Length> paddingLeft = new CSSProperty<>("padding-left", Interpolators.LENGTH);

    // background-color, background-image?
    static final CSSProperty<Fill> background = new CSSProperty<>("background", Interpolators.FILL);
    static final CSSProperty<Length> cornerRadius = new CSSProperty<>("border-radius", Interpolators.LENGTH);
    static final CSSProperty<Length> minWidth = new CSSProperty<>("min-width", Interpolators.LENGTH);
    static final CSSProperty<Length> minHeight = new CSSProperty<>("min-height", Interpolators.LENGTH);

    // kéne vmi "composite property"
    static final CSSProperty<BorderSpec> borderTop = new CSSProperty<>("border", null);
    static final CSSProperty<BorderSpec> borderRight = new CSSProperty<>("border", null);
    static final CSSProperty<BorderSpec> borderBottom = new CSSProperty<>("border", null);
    static final CSSProperty<BorderSpec> borderLeft = new CSSProperty<>("border", null);

    public static final CSSProperty<Length> gap = new CSSProperty<>("gap", Interpolators.LENGTH);
    static final CSSProperty<Fraction> flexGrow = new CSSProperty<>("flex-grow", Interpolators.FRACTION);

    public static final CSSProperty<Label.TextAlign> textAlign = new CSSProperty<>("text-align", null);
    public static final CSSProperty<FontWeight> fontWeight = new CSSProperty<>("font-weight", null);

    static final CSSProperty<List<TransitionSpec<?>>> transition = new CSSProperty<>("transition", null);

    // NON-STANDARD
    static final CSSProperty<Length> rowHeight = new CSSProperty<>("row-height", Interpolators.LENGTH);
    static final CSSProperty<Fill> cellSeparator = new CSSProperty<>("cell-separator", Interpolators.FILL);

    private final String name;

    public final Interpolator<T> interpolator;

    private CSSProperty(String name, Interpolator<T> interpolator) {
        this.name = name;
        this.interpolator = interpolator;

        properties.put(name, this); // ez így thread-safe? bár valszeg igen
    }


    @Override
    public String toString() {
        return name;
    }

    public static CSSProperty<?> ofName(String propName) {
        CSSProperty<?> prop = properties.get(propName);
        if (prop == null)
            throw new IllegalArgumentException("unknown CSS property: " + propName);
        return prop;
    }
}
