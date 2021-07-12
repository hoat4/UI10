package ui10.renderer.java2d;

import ui10.node.LineNode;
import ui10.node.Node;
import ui10.renderer.Decomposer;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Map;
import java.util.Set;

public class J2DRenderer {

    private final Decomposer decomposer;
    public int canvasWidth, canvasHeight;

    public J2DRenderer(Node root) {
        decomposer = new Decomposer(root, Set.of(J2DStrokeNode.class, J2DFillNode.class), Map.of(
                LineNode.class, n->makeLine((LineNode)n)
        ));
    }

    public void render(Graphics2D g) {
        g.fillRect(0, 0, canvasWidth, canvasHeight);
        for (Node node : decomposer.nodes()) {
            ((J2DNode) node).draw(g);
        }
    }

    private J2DNode makeLine(LineNode line) {
        return new J2DStrokeNode(new BasicStroke(),
                new Line2D.Double(0, 0, line.end().get().x().toDouble(), line.end().get().y().toDouble()),
                Color.BLACK);
    }
}
