package ui10.window;

import ui10.base.EnduringElement;
import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;

public abstract class Desktop {

    // majd később legyen TL helyett ScopeLocal
    // ez biztos ebben az osztályban legyen?
    public static final ThreadLocal<Desktop> THREAD_LOCAL = new ThreadLocal<>();

    public final ObservableList<EnduringElement> windows = new ObservableListImpl<>();
}
