package ui10.decoration.css;

import java.util.List;
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

    default boolean isPseudoElement(String pseudoElementName){
        return false;
    }

    default Optional<Integer> indexInSiblings() {
        return Optional.empty();
    }

    ElementMirror parent();

    static ElementMirror ofElementName(ElementMirror parent, String name) {
        return new ElementMirror() {
            @Override
            public ElementMirror parent() {
                return parent;
            }

            @Override
            public String elementName() {
                return name;
            }
        };
    }

    static ElementMirror ofClassName(ElementMirror parent, String... classNames) {
        List<String> classNameList = List.of(classNames);
        return new ElementMirror() {
            @Override
            public ElementMirror parent() {
                return parent;
            }


            @Override
            public boolean hasClass(String className) {
                return classNameList.contains(className);
            }
        };
    }
}
