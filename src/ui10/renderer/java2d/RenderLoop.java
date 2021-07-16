package ui10.renderer.java2d;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class RenderLoop {

    private final Canvas canvas;

    public RenderLoop(Canvas canvas) {
        this.canvas = canvas;
    }

    public void repaint() {
        canvas.createBufferStrategy(2);
        BufferStrategy b = canvas.getBufferStrategy();
        b.getDrawGraphics();
    }
}
