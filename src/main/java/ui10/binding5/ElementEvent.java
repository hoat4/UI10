package ui10.binding5;

import ui10.base.Element;

public interface ElementEvent {

    Element source();

    interface ChangeEvent<T> extends ElementEvent {

        Element oldValue();

        Element newValue();
    }
}
