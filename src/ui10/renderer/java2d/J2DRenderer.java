package ui10.renderer.java2d;

import ui10.binding.ObservableList;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;
import ui10.nodes.LineNode;
import ui10.node.Node;
import ui10.nodes.RectangleNode;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import static ui10.geom.Point.ORIGO;

public class J2DRenderer {

    public int canvasWidth, canvasHeight;
    private final Node root;
    private final Runnable requestUpdate;

    private Rectangle dirtyRegion;

    public J2DRenderer(Node root, Runnable requestUpdate) {
        this.root = root;
        this.requestUpdate = requestUpdate;
        init(root, new AffineTransform());
    }

    private void init(Node node, AffineTransform transform) {
        if (node != root) {
            if (node.position().get() == null) {
                node.position().subscribe(change -> {
                    if (node.rendererData == null)
                        init(node, transform);
                });
                return;
            }
        }

        if (node instanceof RectangleNode r) {
            FillItem item = new FillItem();
            item.shape = new Rectangle2D.Double(0, 0,
                    r.rectangleSize().get().width().toDouble(),
                    r.rectangleSize().get().height().toDouble());
            item.fill = Color.GREEN;
            initPrimitive(node, item, transform);
        } else if (node instanceof LineNode line) {
            StrokeItem item = new StrokeItem();
            item.stroke = new BasicStroke();
            item.paint = Color.BLACK;
            item.shape = new Line2D.Double(0, 0,
                    line.end().get().x().toDouble(), line.end().get().y().toDouble());
            initPrimitive(node, item, transform);
        } else if (node.children() == null) {
            throw new RuntimeException("not a rendering primitive " +
                    "and not decomposable into children: " + node.children());
        } else {
            node.rendererData = RenderItem.HAS_CHILDREN;
            node.children().enumerateAndSubscribe(ObservableList.simpleListSubscriber(
                    newNode -> init(newNode, transform),
                    remove -> {
                    }));
        }
    }

    private void initPrimitive(Node node, RenderItem renderItem, AffineTransform transform) {
        renderItem.transform = (AffineTransform) transform.clone();
        renderItem.transform.translate(node.position().get().x().toDouble(), node.position().get().y().toDouble());
        node.rendererData = renderItem;
        renderItem.bounds = renderItem.computeBounds(renderItem.transform);
        updateDirtyRegion(J2DUtil.rect(renderItem.bounds));
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

        Node.Layout layout = root.layout(BoxConstraints.fixed(new Size(canvasWidth, canvasHeight, 0)));
        layout.valid.subscribe(v->{
            if (!v.newValue())
                updateDirtyRegion(Rectangle.rect(ORIGO, new ui10.geom.Point(canvasWidth, canvasHeight, 0)));
        });
        layout.apply(ORIGO);

        draw(g, root, true);

        dirtyRegion = null;
    }

    private void draw(Graphics2D g, Node node, boolean root) {
        if (!root)
            g.translate(node.position().get().x().toDouble(), node.position().get().y().toDouble());

        if (node.rendererData instanceof RenderItem)
            ((RenderItem) node.rendererData).draw(g);
        else if (node.rendererData == RenderItem.HAS_CHILDREN)
            for (Node n : node.children())
                draw(g, n, false);
        else
            throw new RuntimeException("unknown render data: " + node.rendererData+" for "+node);

        if (!root)
            g.translate(-node.position().get().x().toDouble(), -node.position().get().y().toDouble());
    }

    public void dispose() {

    }
}
