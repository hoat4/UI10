package ui10.decoration;

import ui10.base.Element;

import java.util.Set;

public interface Style {

    Element wrapContent(Element controlContent);

    void invalidated(Set<?> dirtyProperties);

    DecorationContext decorationContext(); /// ???
}
