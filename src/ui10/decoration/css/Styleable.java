package ui10.decoration.css;

import ui10.decoration.DecorationContext;

public interface Styleable {

    String elementName();

    <T> void setProperty(CSSProperty<T> property, T value, DecorationContext decorationContext);
}
