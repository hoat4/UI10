package ui10.shell.renderer.java2d;

import ui10.base.*;
import ui10.binding9.Bindings;
import ui10.binding9.InvalidationPoint;
import ui10.geom.Point;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import static ui10.binding9.Bindings.onFirstChange;
import static ui10.binding9.Bindings.repeatIfInvalidated;

public class J2DLayoutElement extends J2DRenderableElement<LayoutElement> {


    private final List<J2DRenderableElement<?>> children = new ArrayList<>();

    public J2DLayoutElement(J2DRenderer renderer, LayoutElement node) {
        super(renderer, node);
    }

    @Override
    public void initParent(Element parent) {
        super.initParent(parent);

        repeatIfInvalidated(() -> enumerateChildrenHelper(node, e -> e.initParent(this)));
    }

    @Override
    protected void validateImpl() {
    }

    @Override
    protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        enumerateChildrenHelper(node, e -> e.initParent(this));

        InvalidationPoint ip = new InvalidationPoint();
        ip.subscribe();

        return onFirstChange(() -> LayoutProtocol.BOX.preferredSize(node, constraints, context),
                ip::invalidate);
    }


    /*
    @Override
    public void contentChanged() {
        invalidateRendererData();
    }
     */

    @Override
    protected void drawImpl(Graphics2D g) {
        AffineTransform t = g.getTransform();
        for (J2DRenderableElement<?> item : children) {
            assert item != this;
            // g.setClip
            //g.translate(item., item.y);
            item.draw(g);
            //g.setTransform(t);
        }
    }

    @Override
    protected void onShapeApplied(Shape shape) {
        super.onShapeApplied(shape);

        validateIfNeeded();

        children.clear();

        Bindings.onInvalidated(() -> {
            performLayoutHelper(node, new LayoutContext2(this) {

                @Override
                public void accept(RenderableElement e) {
                    if (e.parentRenderable() == null)
                        throw new IllegalStateException("no parent renderable set for: " + e);
                        // régi komment: this should not occur, but currently does because decoration
                        // e.parent = ui10.base.Container.this;

                    else if (e.parentRenderable() != J2DLayoutElement.this)
                        throw new IllegalStateException(e + " is not a child of " + J2DLayoutElement.this + ", instead child of " + e.parentRenderable());
                    children.add((J2DRenderableElement<?>) e);
                }
            });
        }, this::invalidateRenderableElementAndLayout);
    }

    @Override
    public boolean captureMouseEvent(Point p, List<Element> l) {
        for (int i = children.size() - 1; i >= 0; i--) {
            J2DRenderableElement<?> item = children.get(i);
            if (item.shape.contains(J2DUtil.point(p)) && item.captureMouseEvent(p, l))
                return true;
        }

        l.add(this);
        return true;
    }

    @Override
    public String toString() {
        return "J2DLayoutElement (" + node + ")";
    }
}
