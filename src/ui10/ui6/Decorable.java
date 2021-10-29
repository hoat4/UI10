package ui10.ui6;

import java.util.ArrayList;
import java.util.List;

public abstract class Decorable extends Pane {

    private final List<Decoration> decorations = new ArrayList<>();

    protected abstract Element innerContent();

    protected Element wrapDecoratedInner(Element node) {
        return node;
    }

    @Override
    public Element content() {
        Element n = innerContent();

        for (Decoration d : decorations)
            n = d.decorateInner(this, n);

        n = wrapDecoratedInner(n);

        for (Decoration d : decorations)
            n = d.decorateOuter(this, n);

        return n;
    }
}
