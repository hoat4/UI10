package ui10.nodes2;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.binding.SingleElementObservableList;

public abstract class AbstractSingleNodeLayoutPane extends AbstractLayoutPane{

    // TODO a Layout-ot invalidálni kéne, ha a content megváltozik

    private Pane content;

    public AbstractSingleNodeLayoutPane() {
        super(new SingleElementObservableList<>(null));
        ((SingleElementObservableList<Pane>)children).init(content());
    }

    public AbstractSingleNodeLayoutPane(Pane content) {
        this();
        this.content = content;
    }


    public AbstractSingleNodeLayoutPane(ObservableScalar<? extends Pane> content) {
        this();
        content().bindTo(content);
    }

    public ScalarProperty<Pane> content() {
        return property((AbstractSingleNodeLayoutPane n) -> n.content, (n, v) -> n.content = v);
    }

    protected FrameImpl currentContentFrame() {
        return children().get(0);
    }


}
