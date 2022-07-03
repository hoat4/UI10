package ui10.graphics;

import ui10.base.ElementModel;
import ui10.base.Element;
import ui10.geom.Fraction;

import java.util.Objects;

public class Opacity extends ElementModel {

    public final Element content;
    public final Fraction fraction;

    public Opacity(Element content, Fraction fraction) {
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(fraction, "fraction");

        this.content = content;
        this.fraction = fraction;
    }
}
