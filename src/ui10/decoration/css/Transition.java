package ui10.decoration.css;

import ui10.geom.Fraction;
import ui10.base.Attribute;
import ui10.base.Pane;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

public class Transition<T> extends Attribute {

    public final Pane pane;
    public final TransitionSpec<T> spec;
    private T begin;
    private Fraction progress;
    private T end;

    public ScheduledFuture<?> activeAnimation;

    public Transition(Pane pane, TransitionSpec<T> spec, T begin) {
        this.pane = pane;
        this.spec = spec;
        this.begin = begin;
    }

    public void progress(Fraction fraction) {
        this.progress = fraction;
        pane.requestLayout();
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
                "pane=" + pane +
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
