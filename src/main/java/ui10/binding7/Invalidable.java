package ui10.binding7;

import java.util.*;

public abstract class Invalidable {

    private final Set<InvalidationMark> dirty = new HashSet<>();
    public final List<InvalidationListener> listeners = new ArrayList<>();

    public Set<InvalidationMark> dirtyProperties() {
        return dirty;
    }

    protected void invalidate(InvalidationMark property) {
        boolean notify = dirty.isEmpty();
        dirty.add(property);
        if (notify)
            listeners.forEach(l -> l.onInvalidated(this));
    }

    protected void invalidate(InvalidationMark... properties) {
        boolean notify = dirty.isEmpty();
        dirty.addAll(Arrays.asList(properties));
        if (notify)
            listeners.forEach(l -> l.onInvalidated(this));
    }
}
