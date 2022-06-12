package ui10.layout;

import ui10.base.Element;
import ui10.geom.Axis;
import ui10.geom.Fraction;

import java.util.ArrayList;
import java.util.List;

public class LinearLayoutBuilder {

    private final List<Element> elements = new ArrayList<>();
    private final Axis primaryAxis;

    public LinearLayoutBuilder(Axis primaryAxis) {
        this.primaryAxis = primaryAxis;
    }

    public static LinearLayoutBuilder vertical() {
        return new LinearLayoutBuilder(Axis.VERTICAL);
    }

    public static LinearLayoutBuilder horizontal() {
        return new LinearLayoutBuilder(Axis.HORIZONTAL);
    }

    public LinearLayoutBuilder add(int growFactor, Element element) {
        LinearLayout.LinearLayoutConstraints.of(element).growFactor = Fraction.of(growFactor);
        elements.add(element);
        return this;
    }

    public LinearLayout build() {
        return new LinearLayout(primaryAxis, elements);
    }
}
