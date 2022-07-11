package ui10.shell.renderer.java2d;

import ui10.base.*;
import ui10.binding9.Bindings;
import ui10.binding9.InvalidationPoint;
import ui10.binding9.Observer2;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class J2DLayoutElement extends J2DRenderableElement<LayoutElement> {

    private final List<J2DRenderableElement<?>> children = new ArrayList<>();

    private final Map<BoxConstraints, Size> prefSizeCache = new HashMap<>();
    private final InvalidationPoint prefSizeIP = new InvalidationPoint();
    private final Observer2 prefSizeObserver = new DelayedObserver() {

        @Override
        protected void invalidateImpl() {
            executeObserved((Supplier<Void>) () -> {
                for (Map.Entry<BoxConstraints, Size> entry : prefSizeCache.entrySet()) {
                    Size expectedOutput = entry.getValue();
                    Size actualOutput = LayoutProtocol.BOX.preferredSize(node, entry.getKey(), new LayoutContext1(null));
                    if (!actualOutput.equals(expectedOutput)) {
                        clear();
                        prefSizeCache.clear();
                        prefSizeIP.invalidate();
                        return null;
                    }
                }
                return null;
            }
            );
        }
    };

    public J2DLayoutElement(J2DRenderer renderer, LayoutElement node) {
        super(renderer, node);
    }


    @Override
    protected void enumerateStaticChildren(Consumer<Element> consumer) {
        enumerateChildrenHelper(node, consumer);
    }

    @RepeatedInit
    void initChildrenParent() {
        enumerateChildrenHelper(node, e -> e.initParent(this));
    }

    @Override
    protected void validateImpl() {
    }

    @Override
    protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        enumerateChildrenHelper(node, e -> e.initParent(this));

        prefSizeIP.subscribe();

        return prefSizeCache.computeIfAbsent(constraints, c -> {
            return prefSizeObserver.executeObserved(() -> {
                    return LayoutProtocol.BOX.preferredSize(node, constraints, context);
                }
            );
        });
    }

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

        children.clear();

        Bindings.onInvalidated(() -> {
            performLayoutHelper(node, new LayoutContext2(this) {

                @Override
                public void accept(Element e) {
                    if (e.parentRenderable() == null)
                        throw new IllegalStateException("no parent renderable set for: " + e);
                        // régi komment: this should not occur, but currently does because decoration
                        // e.parent = ui10.base.Container.this;

                    else if (e.parentRenderable() != J2DLayoutElement.this)
                        throw new IllegalStateException(e + " is not a child of " + J2DLayoutElement.this
                                + ", instead child of " + e.parentRenderable());
                    children.add((J2DRenderableElement<?>) e);
                }
            });
        }, this::invalidateRenderableElementAndLayout);
    }

    @Override
    public String toString() {
        return "J2DLayoutElement (" + node + ")";
    }
}
