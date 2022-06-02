package ui10.decoration.d3;

import ui10.base.Element;
import ui10.decoration.css.ElementMirror;

import java.util.Optional;

public class ElementMirrorImpl implements ElementMirror {

    final Element element;

    public ElementMirrorImpl(Element element) {
        this.element = element;
    }

    @Override
    public String elementName() {
        return "Element";
    }

    @Override
    public boolean hasClass(String className) {
        return false;
    }

    @Override
    public boolean hasPseudoClass(String pseudoClass) {
        return false;
    }

    @Override
    public Optional<Integer> indexInSiblings() {
        return Optional.empty();
    }
}
