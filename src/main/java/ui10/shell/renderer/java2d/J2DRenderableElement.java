package ui10.shell.renderer.java2d;

import ui10.base.Element;
import ui10.base.RenderableElement;
import ui10.geom.Point;
import ui10.geom.shape.Shape;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

public abstract class J2DRenderableElement<N extends Element> extends RenderableElement {

    protected final J2DRenderer renderer;
    private boolean valid;
    //    public int x, y;
//    public int width, height;
    public java.awt.Shape shape;
    protected final N node;

    public J2DRenderableElement(J2DRenderer renderer, N node) {
        this.renderer = renderer;
        this.node = node;
    }

    @Override
    public void invalidateRendererData() {
        if (valid) {
            valid = false;
            bufferedImage = null;
            renderer.requestRepaint(); // TODO ez induláskor feleslegesen van meghívva
        }
    }

    @Override
    protected void onShapeApplied(Shape shape) {
        super.onShapeApplied(shape);
        this.shape = J2DUtil.shapeToPath2D(shape);
        validateIfNeeded();
    }

    protected abstract void validateImpl();

    public void draw(Graphics2D g) {
        validateIfNeeded();
        drawImpl(g);
    }

    protected void validateIfNeeded() {
        if (!valid) {
            validateImpl();
            valid = true;
        }
    }

    protected abstract void drawImpl(Graphics2D g);

    public boolean captureMouseEvent(Point p, List<Element> l) {
        l.add(this);
        return true;
    }

    private BufferedImage bufferedImage;

    public Paint asPaint() {
        validateIfNeeded();

        Rectangle bounds = shape.getBounds();
        if (bufferedImage == null) {
            bufferedImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g = bufferedImage.createGraphics();
        g.translate(-bounds.x, -bounds.y);
        draw(g);
        g.dispose();

        try(OutputStream out = Files.newOutputStream(java.nio.file.Path.of("a.png"))) {
            ImageIO.write(bufferedImage, "png",out );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new TexturePaint(bufferedImage, bounds);
    }


    @Override
    public String toString() {
        return getClass().getSimpleName()+" ("+node+")";
    }
}
