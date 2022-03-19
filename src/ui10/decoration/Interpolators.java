package ui10.decoration;

import ui10.geom.Fraction;
import ui10.image.Color;
import ui10.image.RGBColor;
import ui10.decoration.css.Length;

public class Interpolators {

    // TODO double-k eliminálása

    public static final Interpolator<Color> COLOR = (b, e, tf) -> {
        RGBColor c1 = b.toRGBColor(), c2 = e.toRGBColor();
        double t = tf.toDouble(), ct = 1 - t;
        return new RGBColor(c1.red() * ct + c2.red() * t, c1.green() * ct + c2.green() * t,
                c1.blue() * ct + c2.blue() * t, c1.alpha() * ct + c2.alpha() * t);
    };

    public static final Interpolator<Length> LENGTH = (b, e, tf) -> {
        double t = tf.toDouble(), ct = 1 - t;
        return new Length(
                (int) Math.round(b.px() * ct + e.px() * t),
                (int) Math.round(b.em() * ct + e.em() * t),
                (int) Math.round(b.relative() * ct + e.relative() * t)
        );
    };
    public static final Interpolator<Fill> FILL = (a, b, t) -> {
        if (a instanceof Fill.ColorFill ac && b instanceof Fill.ColorFill bc)
            return new Fill.ColorFill(COLOR.interpolate(ac.color(), bc.color(), t));
        else
            return new Fill.InterpolatedFill(a, b, t);
    };

    public static final Interpolator<Fraction> FRACTION = (a, b, t) -> {
        return Fraction.add(a.multiply(t), b.multiply(t.oneMinus()));
    };
}
