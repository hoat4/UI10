package ui10.decoration.css;

public class CSSDecorator {

    //public static final Property<CSSDecorator> DECORATOR_PROPERTY = new Property<>(true);

    final CSSParser css;

    public CSSDecorator(CSSParser css) {
        this.css = css;
    }

    /*
    public void elementEvent(EnduringElement element, ElementEvent event) {
        if (event instanceof ChangeEvent<?> c) {
            if (pseudoClassProviders.stream().anyMatch(p -> p.dependencies().contains(c.property()))) {
                System.out.println(element);
                element.invalidateDecoration();
            }
        }
    }

     */

    public Rule ruleOf(ElementMirror e) {
        Rule rule = new Rule();

        for (Rule r : css.rules) {
            if (r.selector.test(e, this)) {
                rule.putAll(r);
            }
        }

        // TODO rule.applyTransitionsOf(e);

        return rule;
    }
}
