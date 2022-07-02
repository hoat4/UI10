package ui10.shell.awt;

import ui10.base.*;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class UIContextImpl implements UIContext {

    private final EventLoop eventLoop;

    private final List<LayoutTask> layoutTasks = new ArrayList<>();

    private final AWTDesktop desktop;
    private final AWTRenderer renderer;

    public UIContextImpl(AWTDesktop desktop, AWTRenderer renderer) {
        this.eventLoop = desktop.eventLoop();
        this.desktop = desktop;
        this.renderer = renderer;
    }

    @Override
    public EventLoop eventLoop() {
        return eventLoop;
    }

    @Override
    public void requestLayout(LayoutTask task) {
        if (layoutTasks.isEmpty())
            eventLoop.runLater(renderer::draw);

        int i = 0;
        while (i < layoutTasks.size()) {
            LayoutTask t = layoutTasks.get(i);
            if (t.element() == task.element())
                return;
            if (isAncestorOfOrSameAs(task.element(), t.element()))
                break;
            i++;
        }

        layoutTasks.add(i, task);
    }

    public void performLayouts() {
        //System.out.println(layoutTasks);
        while (!layoutTasks.isEmpty())
            layoutTasks.remove(0).task().run();
        //System.out.println(layoutTasks);
    }

    private static boolean isAncestorOfOrSameAs(Element e1, Element e2) {
        while (e2 != null)
            if (e2 == e1)
                return true;
            else
                e2 = e2.parent();
        return false;
    }
}
