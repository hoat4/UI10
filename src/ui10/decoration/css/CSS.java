package ui10.decoration.css;

import ui10.image.Fill;
import ui10.nodes.Border;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class CSS {

    public final Map<String, Rule> rules = new HashMap<>(); // TODO ObservableMap

    public static class Rule {

        public I<Fill> background = u -> null;
        public I<Integer> borderWidth = u -> null;
        public I<Fill> borderFill = u -> null;
        public I<Integer> cornerRadius = u -> null;
    }

    public static CSS parse(Reader in) {
        CSS css = new CSS();
        var visitor = new CSSParser.Visitor() {

            private Rule rule;

            @Override
            public void beginRule(String className) {
                rule = new Rule();
                css.rules.put(className, rule);
            }

            @Override
            public void property(String name, CSSParser p) {
                switch (name) {
                    case "background" -> rule.background = p.parseFill();
                    case "border" -> {
                        I<Border.BorderStyle> borderStyle = p.parseBorder();
                        rule.borderFill = uv -> borderStyle.value(uv).fill();
                        rule.borderWidth = uv -> borderStyle.value(uv).width();
                    }
                    case "corner-radius" -> rule.cornerRadius = p.parseLength(null);
                    default -> throw new CSSScanner.CSSParseException("unknown property: " + name);
                }
            }

            @Override
            public void endRule() {
            }
        };
        new CSSParser(new CSSScanner(in), visitor).parseCSS();
        return css;
    }

    @FunctionalInterface
    public interface I<T> {
        T value(UnitValues uv);
    }
}
