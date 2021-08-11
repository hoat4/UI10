package ui10.nodes;

import ui10.binding.ObservableList;
import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.image.Fill;
import ui10.image.RGBColor;
import ui10.layout.BoxConstraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StrokedRectanglePane extends Pane {

    public final ScalarProperty<Integer> thickness = ScalarProperty.create();
    public final ScalarProperty<Fill> fill = ScalarProperty.create();
    public final ScalarProperty<Integer> radius = ScalarProperty.create();

    public StrokedRectanglePane() {
        this(1, RGBColor.BLACK);
    }

    public StrokedRectanglePane(int thickness, Fill fill) {
        this.thickness.set(thickness);
        this.fill.set(fill);
    }

    public StrokedRectanglePane(ObservableScalar<Integer> thickness, ObservableScalar<? extends Fill> fill) {
        this.thickness.bindTo(thickness);
        this.fill.bindTo(fill);
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        StrokePath path = new StrokePath(fill, thickness);

        return new Layout(ObservableList.ofConstantElement(path)) {

            {
                dependsOn(thickness);
                dependsOn(radius);
            }

            @Override
            protected Size determineSize(BoxConstraints constraints) {
                // TODO itt failolni kéne, ha kisebb
                return constraints.clamp(new Size(thickness.get() * 2, thickness.get() * 2));
            }

            @Override
            protected void layout(Collection<?> updated) {
                List<StrokePath.PathElement> list = new ArrayList<>();
                int t = thickness.get();
                int ht = t / 2;
                int w = bounds.get().size().width();
                int h = bounds.get().size().height();
                int r = radius.get();

                list.add(new StrokePath.MoveTo(new Point(r + ht, ht)));
                list.add(new StrokePath.LineTo(new Point(w - ht - r, ht)));
                list.add(new StrokePath.QuadCurveTo(new Point(w - ht, r + ht), new Point(w - ht, ht)));
                list.add(new StrokePath.LineTo(new Point(w - ht, h - ht - r)));
                list.add(new StrokePath.QuadCurveTo(new Point(w - ht - r, h - ht), new Point(w - ht, h - ht)));
                list.add(new StrokePath.LineTo(new Point(ht + r, h - ht)));
                list.add(new StrokePath.QuadCurveTo(new Point(ht, h - ht - r), new Point(ht, h-ht)));
                list.add(new StrokePath.LineTo(new Point(ht, ht+r)));
                list.add(new StrokePath.QuadCurveTo(new Point(ht + r, ht), new Point(ht, ht)));



                /*list.add(new StrokePath.MoveTo(new Point(ht, ht)));
                list.add(new StrokePath.LineTo(new Point(w - ht, ht)));
                list.add(new StrokePath.LineTo(new Point(w - ht, h - ht)));
                list.add(new StrokePath.LineTo(new Point(ht, h - ht)));
                list.add(new StrokePath.LineTo(new Point(ht, ht)));*/
                path.elements.setAll(list);

                path.bounds.set(bounds.get().atOrigo());
            }
        }.asNodeObservable();
    }
}
