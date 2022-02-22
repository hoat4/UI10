package ui10.ui6.decoration.css;

import ui10.ui6.decoration.BorderSpec;
import ui10.ui6.decoration.Fill;
import ui10.ui6.decoration.Interpolator;
import ui10.ui6.decoration.Interpolators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CSSProperty<T> {

    private static final Map<String, CSSProperty<?>> properties = new HashMap<>();

    static final CSSProperty<Fill> textColor = new CSSProperty<>("color", Interpolators.FILL);
    static final CSSProperty<Length> fontSize = new CSSProperty<>("font-size", Interpolators.LENGTH);

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
    static final CSSProperty<BorderSpec> border = new CSSProperty<>("border", null);

    static final CSSProperty<List<TransitionSpec<?>>> transition = new CSSProperty<>("transition", null);

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
