package ui10.renderer.java2d;

import ui10.binding.ObservableList;
import ui10.window.Desktop;
import ui10.window.Window;
import ui10.nodes.EventLoop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AWTDesktop {

    private final EventLoop eventLoop;
    public final Desktop desktop = new Desktop();

    public AWTDesktop(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }

    {
        desktop.windows.subscribe(
                ObservableList.simpleListSubscriber(this::showWindow, this::hideWindow));
    }

    private void showWindow(Window window) {
        JFrame frame = new JFrame("Ablak");
        window.rendererData = frame;
        NodeRendererComponent comp = new NodeRendererComponent(eventLoop);
        comp.root.bindTo(window.content);
        JLabel label = new JLabel("               szöveg");
        label.setFont(new Font("Segoe UI",0,20));
        frame.setBackground(Color.WHITE);
        frame.getContentPane().setBackground(Color.WHITE);
        label.setBackground(Color.WHITE);
        frame.add(comp);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                desktop.windows.remove(window);
            }
        });
        frame.setVisible(true);
    }

    private void hideWindow(Window window) {
        ((JFrame)window.rendererData).dispose();
    }
}
