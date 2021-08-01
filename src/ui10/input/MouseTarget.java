package ui10.input;

import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;
import ui10.binding.ObservableScalar;
import ui10.pane.Pane;
import ui10.pane.WrapperPane;

public class MouseTarget extends WrapperPane {

    public final ObservableList<MouseEvent.MouseButton> pressedButtons = new ObservableListImpl<>();

    public MouseTarget(Pane pane) {
        super(pane);
    }

    public MouseTarget(ObservableScalar<? extends Pane> content) {
        super(content);
    }

}
