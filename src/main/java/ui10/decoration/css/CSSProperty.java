package ui10.decoration.css;

import ui10.base.TextAlign;
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

    public static final CSSProperty<Fill> textColor = new CSSProperty<>("color", Interpolators.FILL, -1);
    public static final CSSProperty<Length> fontSize = new CSSProperty<>("font-size", Interpolators.LENGTH, -1);


    static final int MARGIN_TOP_DCB_INDEX = 0;
    static final CSSProperty<Length> marginTop = new CSSProperty<>("margin-top", Interpolators.LENGTH, MARGIN_TOP_DCB_INDEX);
    static final int MARGIN_RIGHT_DCB_INDEX = 1;
    static final CSSProperty<Length> marginRight = new CSSProperty<>("margin-right", Interpolators.LENGTH, MARGIN_RIGHT_DCB_INDEX);
    static final int MARGIN_BOTTOM_DCB_INDEX = 2;
    static final CSSProperty<Length> marginBottom = new CSSProperty<>("margin-bottom", Interpolators.LENGTH, MARGIN_BOTTOM_DCB_INDEX);
    static final int MARGIN_LEFT_DCB_INDEX = 3;
    static final CSSProperty<Length> marginLeft = new CSSProperty<>("margin-left", Interpolators.LENGTH, MARGIN_LEFT_DCB_INDEX);
    static final int MARGIN_MASK = 15 << MARGIN_TOP_DCB_INDEX;

    static final int PADDING_TOP_INDEX = 4;
    static final CSSProperty<Length> paddingTop = new CSSProperty<>("padding-top", Interpolators.LENGTH, PADDING_TOP_INDEX);
    static final int PADDING_RIGHT_INDEX = 5;
    static final CSSProperty<Length> paddingRight = new CSSProperty<>("padding-right", Interpolators.LENGTH, PADDING_RIGHT_INDEX);
    static final int PADDING_BOTTOM_INDEX = 6;
    static final CSSProperty<Length> paddingBottom = new CSSProperty<>("padding-bottom", Interpolators.LENGTH, PADDING_BOTTOM_INDEX);
    static final int PADDING_LEFT_INDEX = 7;
    static final CSSProperty<Length> paddingLeft = new CSSProperty<>("padding-left", Interpolators.LENGTH, PADDING_LEFT_INDEX);
    static final int PADDING_MASK = 15 << PADDING_TOP_INDEX;

    // background-color, background-image?
    static final int BACKGROUND_INDEX = 8;
    public static final CSSProperty<Fill> background = new CSSProperty<>("background", Interpolators.FILL, BACKGROUND_INDEX);

    static final int TOP_LEFT_CORNER_RADIUS_INDEX = 9;
    static final CSSProperty<Length> topLeftCornerRadius = new CSSProperty<>("border-top-left-radius", Interpolators.LENGTH, TOP_LEFT_CORNER_RADIUS_INDEX);
    static final int TOP_RIGHT_CORNER_RADIUS_INDEX = 10;
    static final CSSProperty<Length> topRightCornerRadius = new CSSProperty<>("border-top-right-radius", Interpolators.LENGTH, TOP_RIGHT_CORNER_RADIUS_INDEX);
    static final int BOTTOM_LEFT_CORNER_RADIUS_INDEX = 11;
    static final CSSProperty<Length> bottomLeftCornerRadius = new CSSProperty<>("border-bottom-left-radius", Interpolators.LENGTH, BOTTOM_LEFT_CORNER_RADIUS_INDEX);
    static final int BOTTOM_RIGHT_CORNER_RADIUS_INDEX = 12;
    static final CSSProperty<Length> bottomRightCornerRadius = new CSSProperty<>("border-bottom-right-radius", Interpolators.LENGTH, BOTTOM_RIGHT_CORNER_RADIUS_INDEX);
    static final int CORNER_RADIUS_MASK = 15 << TOP_LEFT_CORNER_RADIUS_INDEX;

    static final int MIN_WIDTH_INDEX = 13;
    static final CSSProperty<Length> minWidth = new CSSProperty<>("min-width", Interpolators.LENGTH, MIN_WIDTH_INDEX);
    static final int MIN_HEIGHT_INDEX = 14;
    static final CSSProperty<Length> minHeight = new CSSProperty<>("min-height", Interpolators.LENGTH, MIN_HEIGHT_INDEX);

    // kéne vmi "composite property"
    static final int BORDER_TOP_INDEX = 15;
    static final CSSProperty<BorderSpec> borderTop = new CSSProperty<>("border-top", null, BORDER_TOP_INDEX);
    static final int BORDER_RIGHT_INDEX = 16;
    static final CSSProperty<BorderSpec> borderRight = new CSSProperty<>("border-right", null, BORDER_RIGHT_INDEX);
    static final int BORDER_BOTTOM_INDEX = 17;
    static final CSSProperty<BorderSpec> borderBottom = new CSSProperty<>("border-bottom", null, BORDER_BOTTOM_INDEX);
    static final int BORDER_LEFT_INDEX = 18;
    static final CSSProperty<BorderSpec> borderLeft = new CSSProperty<>("border-left", null, BORDER_LEFT_INDEX);
    static final int BORDER_MASK = 15 << BORDER_TOP_INDEX;

    static final int DCB_PROPS = 19;

    public static final CSSProperty<Length> gap = new CSSProperty<>("gap", Interpolators.LENGTH, -1);
    static final CSSProperty<Fraction> flexGrow = new CSSProperty<>("flex-grow", Interpolators.FRACTION, -1);

    public static final CSSProperty<TextAlign> textAlign = new CSSProperty<>("text-align", null, -1);
    public static final CSSProperty<FontWeight> fontWeight = new CSSProperty<>("font-weight", null, -1);

    static final CSSProperty<List<TransitionSpec<?>>> transition = new CSSProperty<>("transition", null, -1);

    // NON-STANDARD
    static final CSSProperty<Length> rowHeight = new CSSProperty<>("row-height", Interpolators.LENGTH, -1);
    static final CSSProperty<Fill> cellSeparator = new CSSProperty<>("cell-separator", Interpolators.FILL, -1);

    private final String name;

    public final Interpolator<T> interpolator;
    public final int dcbIndex;

    private CSSProperty(String name, Interpolator<T> interpolator, int dcbIndex) {
        this.name = name;
        this.interpolator = interpolator;
        this.dcbIndex = dcbIndex;

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
