package ui10.layout4;

import ui10.ui6.Attribute;
import ui10.ui6.Element;

import java.util.Objects;

public class Weight extends Attribute {

    public final int value;

    public Weight(int value) {
        this.value = value;
    }

    public static int weight(Element e) {
        return e.attributes().stream().filter(w -> w instanceof Weight).findAny()
                .map(a -> ((Weight) a).value).orElse(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weight weight = (Weight) o;
        return value == weight.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
