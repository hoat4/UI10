package ui10.renderer6.java2d;

import ui10.geom.Rectangle;
import ui10.ui6.TextField;

import java.awt.AWTEvent;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;

public class Main6 {

    private Frame frame;
    private final J2DRenderer renderer = new J2DRenderer();

    public static void main(String[] args) {
        new Main6().start();
    }

    private void start() {
        TextField tf = new TextField();

        renderer.root = renderer.makeItem(tf);
        renderer.root.bounds = new Rectangle(0, 0, 640, 480);

        frame = new FrameImpl();
        renderer.c = frame;
        frame.addNotify();
        frame.setSize(frame.getInsets().left + 640 + frame.getInsets().right,
                frame.getInsets().top + 480 + frame.getInsets().bottom);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        renderer.requestRepaint();
    }

    private class FrameImpl extends Frame {

        public FrameImpl() throws HeadlessException {
            enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        }

        @Override
        public void paint(Graphics g) {
            renderer.requestRepaint();
        }

        @Override
        protected void processWindowEvent(WindowEvent e) {
            if (e.getID() == WindowEvent.WINDOW_CLOSING) {
                dispose();
                System.exit(0);
            }
        }
    }

}
