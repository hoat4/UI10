package ui10.window;

import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;

public class Desktop {

    public final ObservableList<Window> windows = new ObservableListImpl<>();

    {
        windows.subscribe(ObservableList.simpleListSubscriber(w -> w.shown.set(true), w -> w.shown.set(false)));
    }
}
