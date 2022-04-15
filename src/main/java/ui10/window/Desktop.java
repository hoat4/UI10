package ui10.window;

import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;

public abstract class Desktop {

    public final ObservableList<Window> windows = new ObservableListImpl<>();
}
