package ui10.renderer;

import jdk.jshell.DeclarationSnippet;
import ui10.binding.ListChange;
import ui10.binding.ObservableList;
import ui10.node.Desktop;
import ui10.node.Window;
import ui10.renderer.java2d.NodeRendererComponent;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AWTDesktop {

    public final Desktop desktop = new Desktop();

    {
        desktop.windows().subscribe(
                ObservableList.simpleListSubscriber(this::showWindow, this::hideWindow));
    }

    private void showWindow(Window window) {
        JFrame frame = new JFrame("Ablak");
        window.rendererData = frame;
        NodeRendererComponent comp = new NodeRendererComponent();
        comp.root.bindTo(window.content());
        frame.add(comp);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                desktop.windows().remove(window);
            }
        });
        frame.setVisible(true);
    }

    private void hideWindow(Window window) {
        ((JFrame) window.rendererData).dispose();
    }
}
