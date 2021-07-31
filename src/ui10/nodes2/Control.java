package ui10.nodes2;

import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;
import ui10.binding.Scope;
import ui10.decoration.Decorable;
import ui10.decoration.Decoration;
import ui10.layout.BoxConstraints;

import static ui10.geom.Point.ORIGO;

public abstract class Control extends AbstractPane implements Decorable {

    private final ObservableList<Decoration> decorations = new ObservableListImpl<>();

    protected abstract Pane makeContent();

    /**
     * to be overridden if needed, by default it does nothing
     */
    protected Pane wrapDecoratedContent(Pane decoratedContent) {
        return decoratedContent;
    }

    @Override
    public ObservableList<Decoration> decorations() {
        return decorations;
    }

    @Override
    protected ObservableList<? extends FrameImpl> makeChildList() {
        // TODO ezt frissítsük ha a decorationök invalidálódnak
        Scope scope = new Scope();
        Pane pane = makeContent();
        for (Decoration decoration : decorations) {
            pane = decoration.decorateContent(this, pane, scope);
        }
        pane = wrapDecoratedContent(pane);

        return ObservableList.ofConstantElement(new FrameImpl(pane));
    }

    @Override
    public AbstractLayout computeLayout(BoxConstraints constraints) {
        Frame.FrameAndLayout contentLayout = children().get(0).layout(constraints);
        return new AbstractLayout(constraints, contentLayout.size()) {
            @Override
            public void apply() {
                applyChild(contentLayout, ORIGO);
            }
        };
    }
}
