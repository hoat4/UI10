package ui10.renderer6.java2d;

import ui10.geom.IntTransformationMatrix;
import ui10.geom.Transformation;
import ui10.geom.shape.Path;
import ui10.geom.shape.Shape;
import ui10.geom.shape.StandardPathElement;
import ui10.input.pointer.MouseEvent;
import ui10.ui6.Control;
import ui10.ui6.RenderableElement;
import ui10.ui6.RendererData;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.List;

public abstract class Item<N extends RenderableElement> implements RendererData {

    protected final J2DRenderer renderer;
    private boolean valid;
    //    public int x, y;
//    public int width, height;
    public java.awt.Shape shape;
    protected final N node;

    public Item(J2DRenderer renderer, N node) {
        this.renderer = renderer;
        this.node = node;
        node.rendererData = this;
    }

    @Override
    public void invalidateRendererData() {
        if (valid) {
            valid = false;
            //bufferedImage = null;
            renderer.requestRepaint();
        }
    }

    private void validate() {
        Shape shape = node.shape();
        PathBuilder pb = new PathBuilder(IntTransformationMatrix.IDENTITY, null);
        shape.outline().iterate(pb);
        this.shape = pb.p;

        validateImpl();
    }

    protected abstract void validateImpl();

    public void draw(Graphics2D g) {
        if (!valid) {
            validate();
        }
        drawImpl(g);
        valid = true;
    }

    protected abstract void drawImpl(Graphics2D g);

    public boolean captureMouseEvent(MouseEvent p, List<Control> l) {
        return false;
    }

    // private BufferedImage bufferedImage;

    /*
    public Paint asPaint() {
        if (bufferedImage == null) {
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g = bufferedImage.createGraphics();
        draw(g);
        g.dispose();
        return new TexturePaint(bufferedImage, new Rectangle(width, height));
    }
     */


    private static class PathBuilder extends Path.PathConsumer {

        public final Path2D.Double p = new Path2D.Double();
        private boolean first = true;

        public PathBuilder(Transformation transformation, Shape clip) {
            super(transformation, clip);
        }

        @Override
        protected void addPointImpl(ui10.geom.Point point) {
            if (first) {
                p.moveTo(point.x(), point.y());
                first = false;
            } else
                p.lineTo(point.x(), point.y());
        }

        @Override
        public void addSubpath(Path path) {
            if (path instanceof StandardPathElement.QuadCurveTo) {
                StandardPathElement.QuadCurveTo q = (StandardPathElement.QuadCurveTo) path;
                p.quadTo(q.control().x(), q.control().y(), q.p().x(), q.p().y());
            } else
                super.addSubpath(path);
        }
    }
}
