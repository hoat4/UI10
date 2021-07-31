package ui10.renderer.java2d;

import ui10.binding.ObservableList;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.input.MouseTarget;
import ui10.layout.BoxConstraints;
import ui10.nodes2.*;
import ui10.nodes2.Frame;
import ui10.nodes2.FrameImpl;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static ui10.geom.Point.ORIGO;

public class J2DRenderer {

    public int canvasWidth, canvasHeight;
    private final Frame root;
    private final Runnable requestUpdate;
    final List<AWTMouseTarget> mouseTargets = new ArrayList<>();

    private Rectangle dirtyRegion;

    public J2DRenderer(Pane root, Runnable requestUpdate, Component awtComponent) {
        this.root = new FrameImpl(root);
        this.requestUpdate = requestUpdate;

        init(this.root, new AffineTransform());
    }

    private Object renderItem(Pane pane) {
        return pane.extendedProperties().get(J2DRenderer.class);
    }

    private void init(Frame frame, AffineTransform transform) {
        Pane pane = frame.pane().get();

        if (frame.bounds().get() == null) {
            frame.bounds().subscribe(change -> {
                if (renderItem(pane) == null)
                    init(frame, transform);
            });
            return;
        }


AffineTransform t = (AffineTransform) transform.clone();
        t.translate(
                frame.bounds().get().topLeft().x().toDouble(),
                frame.bounds().get().topLeft().y().toDouble());

        Size size = frame.bounds().get().size();

        if (pane instanceof FilledPane r) {
            FillItem item = new FillItem();
            item.shape = new Rectangle2D.Double(0, 0,
                    size.width().toDouble(),
                    size.height().toDouble());
            item.fill = J2DUtil.color(r.color().get());
            r.color().subscribe(change -> {
                item.fill = J2DUtil.color(r.color().get());
                updateDirtyRegion(J2DUtil.rect(item.bounds));
            });
            initPrimitive(frame, item, t);
        } else if (pane instanceof LinePane line) {
            StrokeItem item = new StrokeItem();
            item.stroke = new BasicStroke();
            item.paint = Color.BLACK;
            item.shape = new Line2D.Double(0, 0,
                    size.width().toDouble(), size.height().toDouble());
            initPrimitive(frame, item, t);
        } else if (pane instanceof TextPane text) {
            TextItem item = new TextItem();
            item.font = (AWTTextStyle) text.textStyle().get();
            item.text = text.text().get();
            initPrimitive(frame, item, t);
        } else if (pane.children() == null) {
            throw new RuntimeException("not a rendering primitive " +
                    "and not decomposable into children: " + pane.children());
        } else {
            if (pane instanceof MouseTarget mt) {
                // TOOD tetszőleges shape lehessen
                Rectangle2D rect = new Rectangle2D.Double(0, 0, size.width().toDouble(), size.height().toDouble());
                mouseTargets.add(new AWTMouseTarget(t.createTransformedShape(rect), mt));
            }

            pane.extendedProperties().put(J2DRenderer.class, RenderItem.HAS_CHILDREN);
            pane.children().enumerateAndSubscribe(ObservableList.simpleListSubscriber(
                    newPane -> init(newPane, t),
                    remove -> {
                    }));
        }
    }

    private void initPrimitive(Frame frame, RenderItem renderItem, AffineTransform transform) {
        renderItem.transform = transform;
        frame.pane().get().extendedProperties().put(J2DRenderer.class, renderItem);
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

        Size canvasSize = new Size(canvasWidth, canvasHeight);
        Frame.FrameAndLayout layout = root.layout(BoxConstraints.fixed(canvasSize));
        layout.frame().bounds().set(new Rectangle(ORIGO, canvasSize));
        layout.paneLayout().valid().subscribe(v -> {
            if (!v.newValue())
                updateDirtyRegion(Rectangle.rect(ORIGO, new ui10.geom.Point(canvasWidth, canvasHeight)));
        });
        layout.paneLayout().apply();

        draw(g, root, true);

        dirtyRegion = null;
    }

    private void draw(Graphics2D g, Frame frame, boolean root) {
        Rectangle bounds = frame.bounds().get();
        if (bounds == null)
            throw new IllegalStateException("frame has no bounds: " + frame);

        if (!root)
            g.translate(bounds.topLeft().x().toDouble(),
                    bounds.topLeft().y().toDouble());

        Object renderItem = renderItem(frame.pane().get());
        if (renderItem instanceof RenderItem ri)
            ri.draw(g);
        else if (renderItem == RenderItem.HAS_CHILDREN)
            for (Frame childFrame : frame.pane().get().children())
                draw(g, childFrame, false);
        else
            System.err.println("unknown render data: " + renderItem + " for " + frame);

        if (!root)
            g.translate(-bounds.topLeft().x().toDouble(),
                    -bounds.topLeft().y().toDouble());
    }

    public void dispose() {

    }
}
