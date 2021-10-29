package ui10.renderer6.java2d;

import ui10.geom.Point;
import ui10.ui6.graphics.LinearGradient;

import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;

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

        p = new LinearGradientPaint(
                node.start().x(), node.start().y(),
                node.end().x(), node.end().y(),
                fractions, colors);
    }

    @Override
    protected void drawImpl(Graphics2D g) {
        g.setPaint(p);
        g.fill(shape);
    }
}
