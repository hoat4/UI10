package ui10.animation;

import ui10.binding.ScalarProperty;

import ui10.geom.Fraction;
import ui10.geom.Point;
import ui10.nodes.EventLoop;

import java.time.Duration;

public class Animation {

    public static <T> void playTransition(ScalarProperty<T> prop,
                                          Interpolator<T> interpolator,
                                          EasingFunction easingFunction,
                                          EventLoop eventLoop,
                                          T begin, T end, Duration duration) {
        eventLoop.beginAnimation(duration, t -> {
            Fraction t2 = easingFunction.curve(t);
            //System.out.println(t+" -> "+t2);
            prop.set(interpolator.interpolate(begin, end, t2));
        });
    }

    @FunctionalInterface
    public interface Interpolator<T> {

        T interpolate(T start, T end, Fraction t);

        Interpolator<Integer> FOR_NUMBERS = (a, b, t) -> t.interpolate(a, b);

        Interpolator<Point> FOR_POINTS = (a, b, t) -> new Point(
                FOR_NUMBERS.interpolate(a.x(), b.x(), t),
                FOR_NUMBERS.interpolate(a.y(), b.y(), t)
        );
    }

    @FunctionalInterface
    public interface EasingFunction {
        Fraction curve(Fraction t);

        EasingFunction LINEAR = x -> x;

        EasingFunction VACAK = x -> Fraction.of(Math.sin((x.toDouble() - .5) * Math.PI)/2+.5,  x.denominator());
    }
}
