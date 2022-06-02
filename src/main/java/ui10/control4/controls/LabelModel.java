package ui10.control4.controls;

import ui10.base.ElementModel;

public abstract class LabelModel extends ElementModel<LabelModel.LabelModelListener> {

    public abstract String text();

    public interface LabelModelListener extends ElementModelListener {

        void textChanged();
    }
}
