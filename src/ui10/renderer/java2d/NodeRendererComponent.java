package ui10.renderer.java2d;

import ui10.binding.ScalarProperty;
import ui10.node.Node;

import javax.swing.*;
import java.awt.*;

public class NodeRendererComponent extends JComponent {

    public final ScalarProperty<Node> root = ScalarProperty.create();

    {
        root.subscribe(evt -> repaint());
    }

    @Override
    protected void paintComponent(Graphics g) {
        J2DRenderer renderer = new J2DRenderer(root.get());
        renderer.canvasWidth = getWidth();
        renderer.canvasHeight = getHeight();
        renderer.render((Graphics2D) g);
    }
}
