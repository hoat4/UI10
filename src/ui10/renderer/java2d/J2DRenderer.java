package ui10.renderer.java2d;

import ui10.binding.ObservableList;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.input.pointer.MouseTarget;
import ui10.layout.BoxConstraints;
import ui10.nodes2.*;
import ui10.pane.Frame;
import ui10.pane.FrameImpl;
import ui10.pane.Pane;

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
    private final Frame root;
    private final Runnable requestUpdate;
    private final PaneRendererComponent awtComponent;
    final List<ParentRenderItem> mouseTargets = new ArrayList<>();

    private Rectangle dirtyRegion;

    public J2DRenderer(Pane root, Runnable requestUpdate, PaneRendererComponent awtComponent) {
        this.root = new FrameImpl(root);
        this.requestUpdate = requestUpdate;
        this.awtComponent = awtComponent;

        init(this.root, new AffineTransform(), null);
    }

    private RenderItem renderItem(Pane pane) {
        return (RenderItem) pane.extendedProperties().get(J2DRenderer.class);
    }

    private void init(Frame frame, AffineTransform transform, RenderItem parent) {
        Pane pane = frame.pane().get();
        pane.inputEnvironment().set(awtComponent.inputEnvironment);

        if (frame.bounds().get() == null) {
            frame.bounds().subscribe(change -> {
                if (renderItem(pane) == null)
                    init(frame, transform, parent);
            });
            return;
        }


        AffineTransform t = (AffineTransform) transform.clone();
        t.translate(
                frame.bounds().get().topLeft().x().toDouble(),
                frame.bounds().get().topLeft().y().toDouble());

        frame.bounds().subscribe(e -> {
            recomputeBounds(frame);
        });

        RenderItem renderItem = makeRenderItem(frame, pane);
        frame.pane().get().extendedProperties().put(J2DRenderer.class, renderItem);
        renderItem.parent = parent;
        renderItem.transform = transform;
        renderItem.bounds = renderItem.computeBounds(renderItem.transform);
        updateDirtyRegion(J2DUtil.rect(renderItem.bounds));

        if (pane.children() != null)
            pane.children().enumerateAndSubscribe(ObservableList.simpleListSubscriber(
                    newPane -> init(newPane, t, renderItem),
                    remove -> {
                    }));
    }

    private RenderItem makeRenderItem(Frame frame, Pane pane) {
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
            return item;
        } else if (pane instanceof LinePane line) {
            StrokeItem item = new StrokeItem();
            item.stroke = new BasicStroke();
            item.paint = Color.BLACK;
            item.shape = new Line2D.Double(0, 0,
                    size.width().toDouble(), size.height().toDouble());
            return item;
        } else if (pane instanceof TextPane text) {
            TextItem item = new TextItem();
            item.font = (AWTTextStyle) text.textStyle().get();
            item.text = text.text().get();
            text.text().subscribe(change -> {
                item.text = text.text().get();
                updateDirtyRegion(J2DUtil.rect(item.bounds));
            });
            return item;
        } else if (pane.children() == null) {
            throw new RuntimeException("not a rendering primitive " +
                    "and not decomposable into children: " + pane.children());
        } else {
            ParentRenderItem r = new ParentRenderItem(frame);

            if (pane instanceof MouseTarget mt) {
                r.mouseTarget = mt;
                mouseTargets.add(r);
            }

            return r;
        }
    }

    private void recomputeBounds(Frame frame) {
        RenderItem renderItem = renderItem(frame.pane().get());
        Objects.requireNonNull(renderItem);

        updateDirtyRegion(J2DUtil.rect(renderItem.bounds));
        renderItem.transform = renderItem.parent == null ? new AffineTransform() :
                (AffineTransform) renderItem.parent.transform.clone();
        renderItem.transform.translate(frame.bounds().get().topLeft().x().toDouble(),
                frame.bounds().get().topLeft().y().toDouble());

        renderItem.bounds = renderItem.computeBounds(renderItem.transform);
        updateDirtyRegion(J2DUtil.rect(renderItem.bounds));

        if (frame.pane().get().children() != null)
            for (Frame child : frame.pane().get().children())
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

        RenderItem renderItem = renderItem(frame.pane().get());
        if (renderItem == null) {
            System.err.println("unknown render data: " + renderItem + " for " + frame);
        } else if (renderItem instanceof ParentRenderItem)
            for (Frame childFrame : frame.pane().get().children())
                draw(g, childFrame, false);
        else
            renderItem.draw(g);

        if (!root)
            g.translate(-bounds.topLeft().x().toDouble(),
                    -bounds.topLeft().y().toDouble());
    }

    public void dispose() {

    }
}
