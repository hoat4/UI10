package ui10.decoration.css;

import java.util.Optional;
import java.util.OptionalInt;

public interface ElementMirror {

    default String elementName() {
        return null;
    }

    default boolean hasClass(String className) {
        return false;
    }

    default boolean hasPseudoClass(String pseudoClass) {
        return false;
    }

    boolean isPseudoElement(String pseudoElementName);

    Optional<Integer> indexInSiblings();

    ElementMirror parent();
}
