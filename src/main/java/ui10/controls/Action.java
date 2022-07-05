package ui10.controls;

import ui10.base.Element;

public interface Action {

    void performAction();

    String text();

    default boolean enabled() {
        return true;
    }

    default String description() { // FX-ben "longText"
        return null;
    }

    default Element icon() { // FX-ben "graphic"
        return null;
    }
/*
    default KeyCombination accelerator() {
        return null;
    }
*/
    interface ActionModelListener {

        void textChanged();

        void enabledChanged();

        void descriptionChanged();

        void iconChanged();

        void acceleratorChanged();
    }
}
