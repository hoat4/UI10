package ui10.renderer.java2d;

import ui10.input.MouseTarget;

import java.awt.*;

public class AWTMouseTarget {
    public final Shape shape;
    public final MouseTarget mouseTarget;

    public AWTMouseTarget(Shape shape, MouseTarget mouseTarget) {
        this.shape = shape;
        this.mouseTarget = mouseTarget;
    }
}
