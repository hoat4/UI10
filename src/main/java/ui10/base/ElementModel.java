package ui10.base;

import ui10.binding5.ReflectionUtil;
import ui10.geom.Point;
import ui10.geom.shape.Shape;

import java.util.function.Consumer;

public non-sealed abstract class ElementModel extends Element {

    private Element view;

    public void initParent(Element parent) {
        Element e = parent;
        if (e == this.parent)
            return;

        if (view != null)
            throw new IllegalStateException(this + " already has view: " + view + " (old parent: " + this.parent + ", new parent: " + parent + ")");

        this.parent = e;

        // TODO ez a predicate nem jó ha csak megváltoztattuk a parentet
        ReflectionUtil.invokeAnnotatedMethods(this, Element.OnChange.class,
                ann -> !lookupMultiple(ann.value()).isEmpty());

        initBeforeView();

        view = ViewProvider.makeView(this, lookupMultiple(ViewProvider.class));
        view.initParent(this);
    }

    protected void initBeforeView() {}

    @Override
    protected final void enumerateStaticChildren(Consumer<Element> consumer) {
        consumer.accept(view());
    }

    // TODO mi van ha változik a view?
    @Override
    public RenderableElement renderableElement() {
        return view().renderableElement();
    }

    // ez így nem jó, hogy dekorált elementmodeleken is látszik ez a függvény
    public Element view() {
        if (view == null)
            throw new IllegalStateException("no view associated with " + this + " (parent: " + parent + ")");
        return view;
    }

    public Shape getShapeOrFail() {
        return view().getShapeOrFail();
    }

    protected void preShapeChange(Shape shape) {
    }

    @Override
    protected final void applyShape(Shape shape, LayoutContext2 context) {
        preShapeChange(shape);
        context.placeElement(view(), shape);
    }

    @Override
    public ContentEditable.ContentPoint pickPosition(Point point) {
        return view().pickPosition(point);
    }

    @Override
    public Shape shapeOfSelection(ContentEditable.ContentRange<?> range) {
        return view().shapeOfSelection(range);
    }
}
