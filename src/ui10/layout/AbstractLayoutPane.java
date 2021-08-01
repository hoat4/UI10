package ui10.layout;

import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;
import ui10.pane.AbstractPane;
import ui10.pane.FrameImpl;
import ui10.pane.Pane;

import java.util.Arrays;

public abstract class AbstractLayoutPane extends AbstractPane {

    public final ObservableList<Pane> children;

    protected AbstractLayoutPane() {
        children = new ObservableListImpl<>();
    }

    protected AbstractLayoutPane(Pane... children) {
        this.children = new ObservableListImpl<>();
        this.children.addAll(Arrays.asList(children));
    }

    protected AbstractLayoutPane(ObservableList<Pane> children) {
        this.children = children;
    }

    @Override
    protected ObservableList<? extends FrameImpl> makeChildList() {
        return children.streamBinding().map(FrameImpl::new).toList();
    }

}
