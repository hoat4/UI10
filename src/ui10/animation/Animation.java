package ui10.animation;

import ui10.binding.ScalarProperty;
import ui10.geom.FloatingPointNumber;
import ui10.geom.Num;
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
            Num t2 = easingFunction.curve(t);
            //System.out.println(t+" -> "+t2);
            prop.set(interpolator.interpolate(begin, end, t2));
        });
    }

    @FunctionalInterface
    public interface Interpolator<T> {

        T interpolate(T start, T end, Num t);

        Interpolator<Num> FOR_NUMBERS = (a, b, t) -> new FloatingPointNumber(
                a.toDouble() * (1 - t.toDouble()) + b.toDouble() * t.toDouble());

        Interpolator<Point> FOR_POINTS = (a, b, t) -> new Point(
                FOR_NUMBERS.interpolate(a.x(), b.x(), t),
                FOR_NUMBERS.interpolate(a.y(), b.y(), t)
        );
    }

    @FunctionalInterface
    public interface EasingFunction {
        Num curve(Num t);

        EasingFunction LINEAR = x -> x;

        EasingFunction VACAK = x -> new FloatingPointNumber(Math.sin((x.toDouble() - .5) * Math.PI)/2+.5);
    }
}
