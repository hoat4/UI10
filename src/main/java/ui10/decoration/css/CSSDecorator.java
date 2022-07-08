package ui10.decoration.css;

import java.net.URISyntaxException;
import java.nio.file.Path;

public class CSSDecorator {

    final CSSParser css;

    public CSSDecorator(CSSParser css) {
        this.css = css;
    }

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

    public Path resource(String name) {
        try {
            // TODO
            return Path.of(getClass().getResource("/ui10/theme/modena-imitation/" + name).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
