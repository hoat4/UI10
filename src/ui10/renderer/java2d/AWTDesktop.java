package ui10.renderer.java2d;

import ui10.binding.ObservableList;
import ui10.node.EventLoop;
import ui10.nodes2.Desktop;
import ui10.nodes2.Window;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AWTDesktop {

    private final EventLoop eventLoop;
    public final Desktop desktop = new Desktop();

    public AWTDesktop(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }

    {
        desktop.windows().subscribe(
                ObservableList.simpleListSubscriber(this::showWindow, this::hideWindow));
    }

    private void showWindow(Window window) {
        JFrame frame = new JFrame("Ablak");
        window.extendedProperties().put(JFrame.class, frame);
        window.shown().set(true);
        PaneRendererComponent comp = new PaneRendererComponent(eventLoop);
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
        window.shown().set(false);
        ((JFrame) window.extendedProperties().get(JFrame.class)).dispose();
    }
}
