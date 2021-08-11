package ui10.renderer.java2d;

import ui10.binding.ObservableList;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.input.EventTarget;
import ui10.nodes.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ui10.renderer.java2d.J2DUtil.i2px;
import static ui10.renderer.java2d.J2DUtil.px2i;

public class J2DRenderer {

    public int canvasWidth, canvasHeight;
    private final Node root;
    private final Runnable requestUpdate;
    private final NodeRendererComponent awtComponent;
    final List<ParentRenderItem> mouseTargets = new ArrayList<>();

    private Rectangle dirtyRegion;

    public J2DRenderer(Node root, Runnable requestUpdate, NodeRendererComponent awtComponent) {
        this.root = root;
        this.requestUpdate = requestUpdate;
        this.awtComponent = awtComponent;

        init(this.root, new AffineTransform(), null);
    }

    private Point pos(Node n) {
        return n.bounds.get().topLeft();
    }

    private Size size(Node n) {
        return n.bounds.get().size();
    }

    private void init(Node node, AffineTransform transform, RenderItem parent) {
        node.context.set(awtComponent.context);

        if (node.bounds.get() == null) {
            node.bounds.subscribe(change -> {
                if (node.rendererData == null)
                    init(node, transform, parent);
            });
            return;
        }


        AffineTransform t = (AffineTransform) transform.clone();
        t.translate(i2px(pos(node).x()), i2px(pos(node).y()));

        RenderItem renderItem = makeRenderItem(node);
        node.rendererData = renderItem;
        renderItem.parent = parent;
        renderItem.transform = transform;
        renderItem.bounds = renderItem.computeBounds(renderItem.transform);
        updateDirtyRegion(J2DUtil.rect(renderItem.bounds));

        node.bounds.subscribe(e -> recomputeBounds(node));

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
        Size size = size(node);

        if (node instanceof FilledRectanglePane r) {
            FillItem item = new FillItem();
            item.fill = J2DUtil.asPaint(r.fill.get());
            item.shape = new RoundRectangle2D.Float(0, 0, i2px(size.width()),
                    i2px(size.height()), i2px(r.radius.get()*2), i2px(r.radius.get())*2);
            r.bounds.map(Rectangle::size).subscribe(s -> {
                item.shape = new RoundRectangle2D.Float(0, 0, i2px(s.newValue().width()), i2px(s.newValue().height()),
                        i2px(r.radius.get() * 2), i2px(r.radius.get()) * 2);
            });
            r.radius.subscribe(s -> {
                Size sz = size(r);
                item.shape = new RoundRectangle2D.Float(0, 0, i2px(sz.width()), i2px(sz.height()),
                        i2px(r.radius.get() * 2), i2px(r.radius.get()) * 2);
                updateDirtyRegion(J2DUtil.rect(item.bounds));
            });
            r.fill.subscribe(change -> {
                item.fill = J2DUtil.asPaint(r.fill.get());
                updateDirtyRegion(J2DUtil.rect(item.bounds));
            });
            return item;
        } else if (node instanceof LinePane line) {
            StrokeItem item = new StrokeItem();
            initLine(line, item);
            line.fill.subscribe(e -> {
                initLine(line, item);
                updateDirtyRegion(J2DUtil.rect(item.bounds));
            });
            line.width.subscribe(e -> {
                initLine(line, item);
                updateDirtyRegion(J2DUtil.rect(item.bounds));
            });
            line.bounds.map(Rectangle::size).subscribe(e -> {
                initLine(line, item);
                // dirty regiont már beállítja a bounds init-beli subscribere
            });
            return item;
        } else if (node instanceof TextPane text) {
            TextItem item = new TextItem();
            item.font = (AWTTextStyle) text.textStyle.get();
            item.text = text.text.get();
            item.color = J2DUtil.color(text.textColor.get());
            text.text.subscribe(change -> {
                item.text = text.text.get();
                updateDirtyRegion(J2DUtil.rect(item.bounds));
            });
            text.textColor.subscribe(e -> {
                item.color = J2DUtil.color(e.newValue());
                updateDirtyRegion(J2DUtil.rect(item.bounds));
            });
            return item;
        } else if (node instanceof StrokePath p) {
            StrokeItem item = new StrokeItem();
            item.paint = J2DUtil.asPaint(p.stroke.get());
            item.stroke = new BasicStroke(i2px(p.thickness.get()));
            initStrokePath(p, item);
            p.stroke.subscribe(f -> {
                item.paint = J2DUtil.asPaint(p.stroke.get());
                updateDirtyRegion(J2DUtil.rect(item.bounds));
            });
            p.elements.subscribe(e -> {
                initStrokePath(p, item);
                updateDirtyRegion(J2DUtil.rect(item.bounds));
            });
            p.thickness.subscribe(f -> {
                item.stroke = new BasicStroke(i2px(p.thickness.get()));
                updateDirtyRegion(J2DUtil.rect(item.bounds));
            });
            return item;
        } else {
            throw new UnsupportedOperationException("unknown rendering primitive: " + node);
        }
    }

    private void initStrokePath(StrokePath s, StrokeItem i) {
        Path2D.Double p = new Path2D.Double();
        i.shape = p;
        for (StrokePath.PathElement e : s.elements) {
            Objects.requireNonNull(e); // ezt elements-be kéne validatorként
            if (e instanceof StrokePath.MoveTo m)
                p.moveTo(i2px(m.p().x()), i2px(m.p().y()));
            else if (e instanceof StrokePath.LineTo l)
                p.lineTo(i2px(l.p().x()), i2px(l.p().y()));
            else if (e instanceof StrokePath.CubicCurveTo c)
                p.curveTo(i2px(c.p().x()), i2px(c.p().y()),
                        i2px(c.control1().x()), i2px(c.control1().y()),
                        i2px(c.control2().x()), i2px(c.control2().y()));
            else if (e instanceof StrokePath.QuadCurveTo c)
                p.quadTo(i2px(c.control().x()), i2px(c.control().y()), i2px(c.p().x()), i2px(c.p().y()));
            else if (e instanceof StrokePath.Close)
                p.closePath();
            else
                throw new UnsupportedOperationException(e.toString());
        }
    }

    private void initLine(LinePane line, StrokeItem item) {
        Size size = size(line);
        item.stroke = new BasicStroke(i2px(line.width.get()));
        item.paint = J2DUtil.asPaint(line.fill.get());
        item.shape = new Line2D.Double(0, 0,
                i2px(size.width() - line.width.get()),
                i2px(size.height() - line.width.get()));
    }

    private void recomputeBounds(Node frame) {
        RenderItem renderItem = (RenderItem) frame.rendererData;
        Objects.requireNonNull(renderItem);

        renderItem.transform = renderItem.parent == null ? new AffineTransform() :
                (AffineTransform) renderItem.parent.transform.clone();
        renderItem.transform.translate(i2px(pos(frame).x()), i2px(pos(frame).y()));

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
        Map<?, ?> desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit().
                getDesktopProperty("awt.font.desktophints");

        if (desktopHints != null) {
            g.setRenderingHints(desktopHints);
        } else {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        g.setColor(Color.WHITE);
        g.fillRect(0, 0, canvasWidth, canvasHeight);

        Size canvasSize = new Size(px2i(canvasWidth), px2i(canvasHeight));

        if (root.bounds.get() == null)
            root.bounds.set(Rectangle.of(canvasSize));

        draw(g, root, true);

        dirtyRegion = null;
    }

    private void draw(Graphics2D g, Node node, boolean root) {
        if (node.bounds.get() == null) {
            System.err.println("Node has no bounds: " + node);
            return;
        }

        Rectangle bounds = node.bounds.get();

        if (!root)
            g.translate(i2px(bounds.topLeft().x()),
                    i2px(bounds.topLeft().y()));

        RenderItem renderItem = (RenderItem) node.rendererData;
        if (renderItem == null) {
            System.err.println("no render data for " + node);
        } else if (renderItem instanceof ParentRenderItem)
            for (Node childNode : node.children())
                draw(g, childNode, false);
        else
            renderItem.draw(g);

        if (!root)
            g.translate(-i2px(bounds.topLeft().x()), -i2px(bounds.topLeft().y()));
    }

    public void dispose() {

    }
}
