package ui10.renderer.java2d;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public abstract class RenderItem {

    public static final Object HAS_CHILDREN = new Object();

    public Rectangle2D bounds;
    public AffineTransform transform;

    abstract Rectangle2D computeBounds(AffineTransform transform);

    public abstract void draw(Graphics2D g);
}
