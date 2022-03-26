package ui10.decoration.css;

import ui10.base.Control;
import ui10.base.Element;
import ui10.controls.Button;
import ui10.decoration.IndexInSiblings;

import java.util.List;

public interface Selector {

    boolean test(Element e, CSSDecorator cssDecorator);

    record ElementSelector(String elementName) implements Selector {
        @Override
        public boolean test(Element e, CSSDecorator cssDecorator) {
            return elementName.equals(e.elementName());
        }
    }

    record ClassSelector(String className) implements Selector {
        @Override
        public boolean test(Element e, CSSDecorator cssDecorator) {
            return e.attributes().contains(new CSSClass(className));
        }
    }

    record PseudoClassSelector(String pseudoClassName) implements Selector {
        @Override
        public boolean test(Element e, CSSDecorator cssDecorator) {
            if (e.attributes().contains(new CSSPseudoClass(pseudoClassName)))
                return true;

            for (PseudoClassProvider p : cssDecorator.pseudoClassProviders)
                if (p.name().equals(pseudoClassName))
                    return p.predicate().test(e);
            return false;
        }
    }

    record ConjunctionSelector(List<Selector> selectors) implements Selector {
        @Override
        public boolean test(Element e, CSSDecorator cssDecorator) {
            for (Selector s : selectors)
                if (!s.test(e, cssDecorator))
                    return false;
            return true;
        }
    }

    record ChildSelector(Selector parentSelector, Selector childSelector) implements Selector {
        @Override
        public boolean test(Element e, CSSDecorator cssDecorator) {
            throw new UnsupportedOperationException();
        }
    }

    record DescendantSelector(Selector ancestorSelector, Selector descendantSelector) implements Selector {
        @Override
        public boolean test(Element e, CSSDecorator cssDecorator) {
            throw new UnsupportedOperationException();
        }
    }

    record NegateSelector(Selector negatedSelector) implements Selector {
        @Override
        public boolean test(Element e, CSSDecorator cssDecorator) {
            return !negatedSelector.test(e, cssDecorator);
        }
    }

    record SiblingSelector(Selector followed, Selector follower) implements Selector {
        @Override
        public boolean test(Element e, CSSDecorator cssDecorator) {
            throw new UnsupportedOperationException();
        }
    }

    record NthChild(int param1, int param2) implements Selector {

        @Override
        public boolean test(Element e, CSSDecorator cssDecorator) {
            return e.attributes().stream().filter(a->a instanceof IndexInSiblings).findAny().map(a->{
                int i = ((IndexInSiblings)a).index;
                if (param1 == 0)
                    return i == param2;
                else
                    return i % param1 == param2;
            }).orElse(false);
        }
    }

}
