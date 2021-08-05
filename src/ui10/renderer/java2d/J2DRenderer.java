package ui10.renderer.java2d;

import ui10.binding.ObservableList;
import ui10.binding.ScalarProperty;
import ui10.binding.Scope;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.input.EventTarget;
import ui10.layout.BoxConstraints;
import ui10.nodes.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ui10.geom.Point.ORIGO;

public class J2DRenderer {

    public int canvasWidth, canvasHeight;
    private final Node root;
    private final Runnable requestUpdate;
    private final NodeRendererComponent awtComponent;
    final List<ParentRenderItem> mouseTargets = new ArrayList<>();

    private Rectangle dirtyRegion;
    private final ScalarProperty<BoxConstraints> layoutConstraints = ScalarProperty.create();

    public J2DRenderer(Node root, Runnable requestUpdate, NodeRendererComponent awtComponent) {
        this.root = root;
        this.requestUpdate = requestUpdate;
        this.awtComponent = awtComponent;

        init(this.root, new AffineTransform(), null);
        root.position.set(ORIGO);
    }

    private void init(Node node, AffineTransform transform, RenderItem parent) {
        node.inputEnvironment.set(awtComponent.inputEnvironment);

        if (!node.layoutActive.get()) {
            node.layoutActive.subscribe(change -> {
                if (node.rendererData == null)
                    init(node, transform, parent);
            });
            return;
        }

        if (node.position.get() == null) {
            System.err.println("Node has no position (2): "+node);
            return;
        }


        AffineTransform t = (AffineTransform) transform.clone();
        t.translate(
                node.position.get().x().toDouble(),
                node.position.get().y().toDouble());

        node.position.subscribe(e -> recomputeBounds(node));
        node.size.subscribe(e -> recomputeBounds(node));

        RenderItem renderItem = makeRenderItem(node);
        node.rendererData = renderItem;
        renderItem.parent = parent;
        renderItem.transform = transform;
        renderItem.bounds = renderItem.computeBounds(renderItem.transform);
        updateDirtyRegion(J2DUtil.rect(renderItem.bounds));

        node.children().enumerateAndSubscribe(ObservableList.simpleListSubscriber(
                newPane -> init(newPane, t, renderItem),
                remove -> {
                }));
    }

    private RenderItem makeRenderItem(Node node) {
        if (node instanceof PrimitiveNode primitiveNode) {
            return makePrimitiveRenderItem(primitiveNode.target);
        } else {
            ParentRenderItem r = new ParentRenderItem(node);

            if (node instanceof EventTarget mt) {
                r.mouseTarget = mt;
                mouseTargets.add(r);
            }

            return r;
        }
    }

    private RenderItem makePrimitiveRenderItem(Node node) {
        Size size = node.size.get();

        if (node instanceof FilledPane r) {
            FillItem item = new FillItem();
            item.shape = new Rectangle2D.Double(0, 0,
                    size.width().toDouble(),
                    size.height().toDouble());
            item.fill = J2DUtil.color(r.color.get());
            r.color.subscribe(change -> {
                item.fill = J2DUtil.color(r.color.get());
                updateDirtyRegion(J2DUtil.rect(item.bounds));
            });
            return item;
        } else if (node instanceof LinePane line) {
            StrokeItem item = new StrokeItem();
            initLine(line, item);
            line.color.subscribe(e -> {
                initLine(line, item);
                updateDirtyRegion(J2DUtil.rect(item.bounds));
            });
            line.width.subscribe(e -> {
                initLine(line, item);
                updateDirtyRegion(J2DUtil.rect(item.bounds));
            });
            line.size.subscribe(e -> {
                initLine(line, item);
                // dirty regiont már beállítja a bounds init-beli subscribere
            });
            return item;
        } else if (node instanceof TextPane text) {
            TextItem item = new TextItem();
            item.font = (AWTTextStyle) text.textStyle.get();
            item.text = text.text.get();
            text.text.subscribe(change -> {
                item.text = text.text.get();
                updateDirtyRegion(J2DUtil.rect(item.bounds));
            });
            return item;
        } else {
            throw new UnsupportedOperationException("unknown rendering primitive: " + node);
        }
    }

    private void initLine(LinePane line, StrokeItem item) {
        Size size = line.size.get();
        item.stroke = new BasicStroke((float) line.width.get().toDouble());
        item.paint = J2DUtil.color(line.color.get());
        item.shape = new Line2D.Double(0, 0,
                size.width().sub(line.width.get()).toDouble(),
                size.height().sub(line.width.get()).toDouble());
    }

    private void recomputeBounds(Node frame) {
        RenderItem renderItem = (RenderItem) frame.rendererData;
        Objects.requireNonNull(renderItem);

        updateDirtyRegion(J2DUtil.rect(renderItem.bounds));
        renderItem.transform = renderItem.parent == null ? new AffineTransform() :
                (AffineTransform) renderItem.parent.transform.clone();
        renderItem.transform.translate(frame.position.get().x().toDouble(),
                frame.position.get().y().toDouble());

        renderItem.bounds = renderItem.computeBounds(renderItem.transform);
        updateDirtyRegion(J2DUtil.rect(renderItem.bounds));

        if (frame.children() != null)
            for (Node child : frame.children())
                recomputeBounds(child);
    }

    private void updateDirtyRegion(Rectangle r) {
        boolean requestUpdate = dirtyRegion == null;
        dirtyRegion = Rectangle.union(dirtyRegion, r);
        if (requestUpdate)
            this.requestUpdate.run();
    }

    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, canvasWidth, canvasHeight);

        Size canvasSize = new Size(canvasWidth, canvasHeight);

        BoxConstraints layoutConstraints = BoxConstraints.fixed(canvasSize);
        if (this.layoutConstraints.get() == null) {
            this.layoutConstraints.set(layoutConstraints);
            root.layoutThread(this.layoutConstraints, true, new Scope());
        }

        draw(g, root, true);

        dirtyRegion = null;
    }

    private void draw(Graphics2D g, Node node, boolean root) {
        if (!node.layoutActive.get()) {
            System.err.println("Node not laid out: " + node);
            return;
        }
        if (node.position.get() == null) {
            System.err.println("Node has no position: " + node);
            return;
        }
        if (node.size.get() == null) {
            System.err.println("Node has no size: " + node);
            return;
        }

        Rectangle bounds = new Rectangle(node.position.get(), node.size.get());

        if (!root)
            g.translate(bounds.topLeft().x().toDouble(),
                    bounds.topLeft().y().toDouble());

        RenderItem renderItem = (RenderItem) node.rendererData;
        if (renderItem == null) {
            System.err.println("no render data for " + node);
        } else if (renderItem instanceof ParentRenderItem)
            for (Node childNode : node.children())
                draw(g, childNode, false);
        else
            renderItem.draw(g);

        if (!root)
            g.translate(-bounds.topLeft().x().toDouble(),
                    -bounds.topLeft().y().toDouble());
    }

    public void dispose() {

    }
}
