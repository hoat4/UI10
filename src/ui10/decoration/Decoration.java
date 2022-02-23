package ui10.decoration;

import ui10.base.Element;
import ui10.base.Pane;

public interface Decoration {

    Element decorate(Pane decorable, Element content);
}
