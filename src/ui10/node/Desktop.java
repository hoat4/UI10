package ui10.node;

import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;

public class Desktop {

    private final ObservableList<Window> windows = new ObservableListImpl<>();

    public ObservableList<Window> windows() {
        return windows;
    }
}
