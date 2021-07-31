package ui10.nodes2;

import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;

import java.util.Arrays;

public abstract class AbstractItemPane<I extends AbstractItemPane.Item> extends AbstractPane {

    private final ObservableList<I> items = new ObservableListImpl<>();

    public AbstractItemPane() {
    }

    public AbstractItemPane(I... items) {
        this.items.addAll(Arrays.asList(items));
    }

    public ObservableList<I> items() {
        return items;
    }

    @Override
    protected ObservableList<? extends FrameImpl> makeChildList() {
        return items.streamBinding().map(Item::frame).toList();
    }

    public static class Item {

        private final Pane pane;
        private final FrameImpl frame;

        public Item(Pane pane) {
            this.pane = pane;
            this.frame = new FrameImpl(pane);
        }

        public Pane pane() {
            return pane;
        }

        public FrameImpl frame() {
            return frame;
        }
    }
}
