package ui10;

import ui10.geom.Point;
import ui10.node.Desktop;
import ui10.node.LineNode;
import ui10.node.Window;
import ui10.renderer.AWTDesktop;

public class Main {

    public static void main(String[] args) {
	    Desktop desktop = new AWTDesktop().desktop;

	    Window window = new Window();
	    window.content().set(new LineNode(new Point(100, 100, 0)));
	    desktop.windows().add(window);
    }
}
