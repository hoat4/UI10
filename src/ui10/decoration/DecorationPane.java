package ui10.decoration;

import ui10.binding.ObservableList;
import ui10.layout.BoxConstraints;
import ui10.nodes2.AbstractPane;
import ui10.nodes2.FrameImpl;
import ui10.nodes2.Pane;

import java.util.Arrays;

public class DecorationPane extends AbstractPane {

    private final FrameImpl content;
    private final ObservableList<Decoration> decorations;

    @SuppressWarnings("unchecked")
    public DecorationPane(Pane content, Decoration... decorations) {
        this.content = new FrameImpl(content);
        this.decorations = (ObservableList<Decoration>) (ObservableList<?>) this.content.pane().transformations();
        this.decorations.addAll(Arrays.asList(decorations));
    }

    public ObservableList<Decoration> decorations() {
        return decorations;
    }

    @Override
    protected ObservableList<? extends FrameImpl> makeChildList() {
        return ObservableList.ofConstantElement(content);
    }

    @Override
    public AbstractLayout computeLayout(BoxConstraints constraints) {
        return AbstractLayout.wrap(content.layout(constraints));
    }
}
