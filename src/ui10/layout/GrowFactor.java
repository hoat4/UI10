package ui10.layout;

import ui10.geom.Fraction;
import ui10.base.Attribute;
import ui10.base.Element;

import java.util.Objects;

public class GrowFactor extends Attribute {

    public static final Fraction DEFAULT = Fraction.WHOLE;

    public final Fraction value;

    public GrowFactor(Fraction value) {
        this.value = value;
    }

    public static Fraction growFactor(Element e) {
        return e.attributes().stream().filter(w -> w instanceof GrowFactor).findAny()
                .map(a -> ((GrowFactor) a).value).orElse(DEFAULT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrowFactor growFactor = (GrowFactor) o;
        return value == growFactor.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
