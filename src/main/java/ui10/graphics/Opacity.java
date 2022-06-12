package ui10.graphics;

import ui10.base.Element;
import ui10.base.ElementModel;
import ui10.base.EnduringElement;
import ui10.base.RenderableElement;
import ui10.geom.Fraction;

import java.util.Objects;

public class Opacity extends ElementModel<Opacity.OpacityElementListener> {

    public final EnduringElement content;
    public final Fraction fraction;

    public Opacity(EnduringElement content, Fraction fraction) {
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(fraction, "fraction");

        this.content = content;
        this.fraction = fraction;
    }

    public interface OpacityElementListener extends ElementModelListener {

        void opacityChanged();
    }
}
