package ui10.nodes;

import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;
import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;

import ui10.geom.Point;
import ui10.geom.Size;
import ui10.image.Fill;
import ui10.image.RGBColor;
import ui10.layout.BoxConstraints;

import java.util.List;



public class StrokePath extends Pane {

    public final ObservableList<PathElement> elements;
    public final ScalarProperty<Fill> stroke = ScalarProperty.create();
    public final ScalarProperty<Integer> thickness = ScalarProperty.create("StrokePath.thickness");

    public StrokePath() {
        elements = new ObservableListImpl<>();
        stroke.set(RGBColor.BLACK);
        thickness.set(1);
    }

    public StrokePath(List<PathElement> elements, Fill stroke, int thickness) {
        this.elements = new ObservableListImpl<>();
        this.elements.addAll(elements);
        this.stroke.set(stroke);
        this.thickness.set(thickness);
    }

    public StrokePath(ObservableScalar<Fill> stroke, ObservableScalar<Integer> thickness) {
        this.elements = new ObservableListImpl<>();
        this.stroke.bindTo(stroke);
        this.thickness.bindTo(thickness);
    }

    public StrokePath(ObservableList<PathElement> elements, ObservableScalar<Fill> stroke, ObservableScalar<Integer> thickness) {
        this.elements = elements;
        this.stroke.bindTo(stroke);
        this.thickness.bindTo(thickness);
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        return ObservableScalar.ofConstant(new PrimitiveNode(this) {
            @Override
            public Size determineSize(BoxConstraints constraints) {
                return elements.stream().
                        filter(p -> p instanceof PointPathElement).
                        map(p -> ((PointPathElement) p).p()).
                        reduce(Point::max).
                        map(Size::of).
                        orElse(Size.ZERO);
            }
        });
    }

    public interface PathElement {
    }

    private interface PointPathElement extends PathElement {
        Point p();
    }


    public record MoveTo(Point p) implements PathElement, PointPathElement {
    }

    public record LineTo(Point p) implements PathElement, PointPathElement {
    }

    public record CubicCurveTo(Point p, Point control1, Point control2) implements PathElement, PointPathElement {
    }

    public record QuadCurveTo(Point p, Point control) implements PathElement, PointPathElement {
    }

    public record Close() implements PathElement {
    }
}
