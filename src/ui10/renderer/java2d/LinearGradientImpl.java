package ui10.renderer.java2d;

import ui10.geom.Point;
import ui10.graphics.LinearGradient;

import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.geom.Rectangle2D;

public class LinearGradientImpl extends Item<LinearGradient> {

    private LinearGradientPaint p;

    public LinearGradientImpl(J2DRenderer renderer, LinearGradient node) {
        super(renderer, node);
    }

    @Override
    protected void validateImpl() {
        float[] fractions = new float[node.stops.size()];
        java.awt.Color[] colors = new java.awt.Color[node.stops.size()];
        int d = Point.distance(node.start(), node.end());
        for (int i = 0; i < node.stops.size(); i++) {
            LinearGradient.Stop stop = node.stops.get(i);
            fractions[i] = (float) stop.pos() / d;
            colors[i] = J2DUtil.color(stop.color());
        }

        Rectangle2D bounds = shape.getBounds2D();
        p = new LinearGradientPaint(
                (float) bounds.getX()+node.start().x(), (float) bounds.getY()+node.start().y(),
                (float) bounds.getX()+node.end().x(), (float) bounds.getY()+node.end().y(),
                fractions, colors);
    }

    @Override
    protected void drawImpl(Graphics2D g) {
        g.setPaint(p);
        g.fill(shape);
    }
}
