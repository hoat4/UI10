package ui10.nodes;

import ui10.input.InputEnvironment;

public class Context {
    public final EventLoop eventLoop;
    public final InputEnvironment inputEnvironment;

    public Context(EventLoop eventLoop, InputEnvironment inputEnvironment) {
        this.eventLoop = eventLoop;
        this.inputEnvironment = inputEnvironment;
    }

    public void addToLayoutQueue(Runnable n) {
eventLoop.runLater(n);
    }
}
