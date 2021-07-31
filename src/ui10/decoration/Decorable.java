package ui10.decoration;

import ui10.binding.ObservableList;
import ui10.nodes2.Pane;

public interface Decorable extends Pane {

    ObservableList<Decoration> decorations();
}
