package ui10.nodes;

import ui10.binding.ObservableList;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

import java.util.ArrayList;
import java.util.List;

public class LayoutNodeImpl extends Node {

    private final Layout layout;

    List<Object> dirty = new ArrayList<>();
    boolean layoutInProgress;
    boolean reqFailedNoCtx;

    private final List<WatchedSize> watchedSizes = new ArrayList<>();

    {
        bounds.subscribe(e -> {
            if (e.oldValue() == null || !e.oldValue().size().equals(e.newValue().size()))
                layout();
        });
        context.subscribe(e -> {
            if (reqFailedNoCtx) {
                requestLayout("got context");
                reqFailedNoCtx = false;
            }
            dirty.clear();
        });
    }

    LayoutNodeImpl(Layout layout) {
        this.layout = layout;
        layout.children.enumerateAndSubscribe(e -> {
            requestLayout("children"); // TODO

            for (var removedNode : e.oldElements())
                removedNode.parent.set(null);
            for (var addedNode : e.newElements())
                addedNode.parent.set(this);
        });
    }

    public void requestLayout(Object origin) {
        if (layoutInProgress)
            throw new IllegalStateException(this + " already has a layout operation in progress " +
                    "(new layout op requested by " + origin + ")");

        if (!dirty.isEmpty() && !reqFailedNoCtx)
            return;

        dirty.add(origin);

        Context ctx = this.context.get();
        if (ctx == null) {
            reqFailedNoCtx = true;
            return;
        }

        LayoutNodeImpl p = parentLayoutNode();

        boolean sizeChanged = isSizeChanged();
        if (sizeChanged)
            watchedSizes.clear();

        if (p != null && sizeChanged)
            p.requestLayout(this);
        else
            ctx.addToLayoutQueue(this::layout);
    }

    private boolean isSizeChanged() {
        for (WatchedSize ws : watchedSizes)
            if (!determineSize(ws.constraints).equals(ws.size))
                return true;
        return false;
    }

    private LayoutNodeImpl parentLayoutNode() {
        Node n = parent.get();
        while (n != null && !(n instanceof LayoutNodeImpl))
            n = n.parent.get();
        return (LayoutNodeImpl) n;
    }

    @Override
    public Size determineSize(BoxConstraints constraints) {
        boolean prev = layoutInProgress;
        layoutInProgress = true;
        try {
            Size s = layout.determineSize(constraints);
            if (!constraints.contains(s))
                throw new RuntimeException("computed size " + s + " doesn't conform to constraints " +
                        constraints + " (node: " + this + ")");

            LayoutNodeImpl p = parentLayoutNode();
            if (p != null && p.layoutInProgress)
                watchedSizes.add(new WatchedSize(constraints, s));

            return s;
        } finally {
            layoutInProgress = prev;
        }
    }

    private void layout() {
        if (layoutInProgress)
            throw new IllegalStateException(this + " already has a layout operation in progress");

        layoutInProgress = true;
        try {
            layout.layout(dirty);

            for (Object o : dirty)
                if (o instanceof LayoutNodeImpl n)
                    n.layout();

            dirty.clear();
        } finally {
            layoutInProgress = false;
        }
    }

    public ObservableList<? extends Node> children() {
        return layout.children;
    }

    @Override
    public String toString() {
        return "LayoutNode (parent=" + (parent.get() == null ? "null" : parent.get().getClass().getSimpleName()) + ")";
    }

    private record WatchedSize(BoxConstraints constraints, Size size) {
    }
}
