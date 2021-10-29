package ui10.ui6;

public interface Decoration {

    Element decorateInner(Decorable decorable, Element content);

    Element decorateOuter(Decorable decorable, Element content);
}
