package ui10.decoration.css;

import java.util.List;

public interface Selector {

    boolean test(ElementMirror e, CSSDecorator cssDecorator);

    record ElementSelector(String elementName) implements Selector {
        @Override
        public boolean test(ElementMirror e, CSSDecorator cssDecorator) {
            return elementName.equals(e.elementName());
        }
    }

    record ClassSelector(String className) implements Selector {
        @Override
        public boolean test(ElementMirror e, CSSDecorator cssDecorator) {
            return e.hasClass(className);
        }
    }

    record PseudoClassSelector(String pseudoClassName) implements Selector {
        @Override
        public boolean test(ElementMirror e, CSSDecorator cssDecorator) {
            return e.hasPseudoClass(pseudoClassName);
        }
    }

    record PseudoElementSelector(Selector selector, String pseudoElementName) implements Selector {
        @Override
        public boolean test(ElementMirror e, CSSDecorator cssDecorator) {
            return e.parent() != null && selector.test(e.parent(), cssDecorator) && e.isPseudoElement(pseudoElementName);
        }
    }

    record ConjunctionSelector(List<Selector> selectors) implements Selector {
        @Override
        public boolean test(ElementMirror e, CSSDecorator cssDecorator) {
            for (Selector s : selectors)
                if (!s.test(e, cssDecorator))
                    return false;
            return true;
        }
    }

    record DisjunctionSelector(List<Selector> selectors) implements Selector {
        @Override
        public boolean test(ElementMirror e, CSSDecorator cssDecorator) {
            for (Selector s : selectors)
                if (s.test(e, cssDecorator))
                    return true;
            return false;
        }
    }

    record ChildSelector(Selector parentSelector, Selector childSelector) implements Selector {
        @Override
        public boolean test(ElementMirror e, CSSDecorator cssDecorator) {
            ElementMirror parent = e.parent();
            return parent != null && parentSelector.test(parent, cssDecorator);
        }
    }

    record DescendantSelector(Selector ancestorSelector, Selector descendantSelector) implements Selector {
        @Override
        public boolean test(ElementMirror e, CSSDecorator cssDecorator) {
            throw new UnsupportedOperationException();
        }
    }

    record NegateSelector(Selector negatedSelector) implements Selector {
        @Override
        public boolean test(ElementMirror e, CSSDecorator cssDecorator) {
            return !negatedSelector.test(e, cssDecorator);
        }
    }

    record SiblingSelector(Selector followed, Selector follower) implements Selector {
        @Override
        public boolean test(ElementMirror e, CSSDecorator cssDecorator) {
            throw new UnsupportedOperationException();
        }
    }

    record NthChild(int param1, int param2) implements Selector {

        @Override
        public boolean test(ElementMirror e, CSSDecorator cssDecorator) {
            return e.indexInSiblings().map(i -> {
                if (param1 == 0)
                    return i == param2;
                else
                    return i % param1 == param2;
            }).orElse(false);
        }
    }

}
