package ui10.ui6.decoration;

import ui10.ui6.Element;
import ui10.ui6.Pane;

public interface Decoration {

    Element decorate(Pane decorable, Element content);
}
