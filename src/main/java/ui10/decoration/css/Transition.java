package ui10.decoration.css;

import ui10.base.EnduringElement;
import ui10.geom.Fraction;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

public class Transition<T>  {

    public final EnduringElement element;
    public final TransitionSpec<T> spec;
    private T begin;
    private Fraction progress;
    private T end;

    public ScheduledFuture<?> activeAnimation;

    public Transition(EnduringElement element, TransitionSpec<T> spec, T begin) {
        this.element = element;
        this.spec = spec;
        this.begin = begin;
    }

    public void progress(Fraction fraction) {
        this.progress = fraction;
    }

    public Fraction progress() {
        return progress;
    }

    public T currentEnd() {
        return end;
    }

    public boolean setEnd(T end) {
        if (Objects.equals(end, this.end))
            return false;

        boolean start = this.end != null;
        if (progress != null)
            this.begin = value();
        this.end = end;
        this.progress = Fraction.ZERO;
        return start;
    }

    public T value() {
        if (end == null)
            return begin;
        return spec.property().interpolator.interpolate(begin, end, progress);
    }

    @Override
    public String toString() {
        return "Transition{" +
                "pane=" + element +
                ", spec=" + spec +
                ", begin=" + begin +
                ", progress=" + progress +
                ", end=" + end +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
