package ui10.shell.awt;

import ui10.base.EventLoop;
import ui10.base.RenderableElement;
import ui10.base.UIContext;
import ui10.shell.renderer.java2d.J2DRenderer;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class UIContextImpl implements UIContext {

    private final EventLoop eventLoop = new EventLoop();

    private final List<LayoutTask> layoutTasks = new ArrayList<>();

    private final AWTRenderer renderer;

    private int inLayout =-1;

    public UIContextImpl(AWTRenderer renderer) {
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
            if (isAncestorOfOrSameAs(t.element(), task.element()))
                return;
        }

        BitSet bitSet = new BitSet();
        for (int i = inLayout + 1; i<layoutTasks.size(); i++) {
            if (isAncestorOfOrSameAs(task.element(), layoutTasks.get(i).element()))
                bitSet.set(i);
        }
        for (int i = bitSet.previousSetBit(bitSet.size()-1); i!= -1; i = bitSet.previousSetBit(i-1))
            layoutTasks.remove(i);
        layoutTasks.add(task);
    }

    public void performLayouts() {
        System.out.println(layoutTasks);
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
                e2 = e2.parent;
        return false;
    }
}
