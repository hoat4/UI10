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

    private int inLayout =-1;

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

        for (LayoutTask t : layoutTasks) {
            if (isAncestorOfOrSameAs(t.element().renderableElement(), task.element().renderableElement()))
                return;
        }

        BitSet bitSet = new BitSet();
        for (int i = inLayout + 1; i<layoutTasks.size(); i++) {
            if (isAncestorOfOrSameAs(task.element().renderableElement(), layoutTasks.get(i).element().renderableElement()))
                bitSet.set(i);
        }
        for (int i = bitSet.previousSetBit(bitSet.size()-1); i!= -1; i = bitSet.previousSetBit(i-1))
            layoutTasks.remove(i);
        layoutTasks.add(task);
    }

    public void performLayouts() {
        //System.out.println(layoutTasks);
        for (inLayout = 0; inLayout < layoutTasks.size(); inLayout++) {
            LayoutTask t = layoutTasks.get(inLayout);
            t.task().run();
        }
        inLayout = -1;
        //System.out.println(layoutTasks);
        layoutTasks.clear();
    }

    private static boolean isAncestorOfOrSameAs(RenderableElement e1, RenderableElement e2) {
        while (e2 != null)
            if (e2 == e1)
                return true;
            else
                e2 = e2.parentRenderable();
        return false;
    }
}
