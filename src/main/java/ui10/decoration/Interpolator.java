package ui10.decoration;

import ui10.geom.Fraction;

public interface Interpolator<T> {

    T interpolate(T begin, T end, Fraction progress);
}
