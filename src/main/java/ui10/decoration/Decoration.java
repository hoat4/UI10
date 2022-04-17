package ui10.decoration;

import ui10.base.Element;
import ui10.base.Container;

public interface Decoration {

    Element decorate(Container decorable, Element content);
}
