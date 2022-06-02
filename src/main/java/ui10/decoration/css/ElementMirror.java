package ui10.decoration.css;

import java.util.Optional;
import java.util.OptionalInt;

public interface ElementMirror {

    String elementName();

    boolean hasClass(String className);

    boolean hasPseudoClass(String pseudoClass);

    Optional<Integer> indexInSiblings();
}
