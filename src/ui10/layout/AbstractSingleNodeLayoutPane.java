package ui10.layout;

import ui10.binding.ObservableScalar;
import ui10.binding.ScalarProperty;
import ui10.binding.SingleElementObservableList;
import ui10.pane.FrameImpl;
import ui10.pane.Pane;

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
