package ui10.decoration.css;

import ui10.base.TextAlign;
import ui10.decoration.BorderSpec;
import ui10.decoration.Fill;
import ui10.decoration.PointSpec;
import ui10.geom.Fraction;
import ui10.graphics.FontWeight;
import ui10.image.Color;
import ui10.image.Colors;
import ui10.image.RGBColor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static ui10.decoration.css.CSSScanner.chToString;
import static ui10.decoration.css.Length.percent;
import static ui10.decoration.css.Length.zero;

public class CSSParser {

    final CSSScanner scanner;
    public final List<Rule> rules = new ArrayList<>();

    public CSSParser(CSSScanner scanner) {
        this.scanner = scanner;
    }

    public void parseCSS() {
        scanner.skipWhitespaces();

        while (scanner.next != -1) {
            //if (scanner.next == '-') {
            //    scanner.expect('-');
            //    String varName = "--"+scanner.readIdentifier();
            //    vars.put(varName, )
            //}


            Selector selector = parseSelector();
            scanner.skipWhitespaces();
            scanner.expect("{");

            Rule rule = new Rule();
            rule.selector = selector;
            rules.add(rule);

            scanner.skipWhitespaces();
            while (scanner.next != '}') {
                var propName = scanner.readIdentifier();
                scanner.skipWhitespaces();
                scanner.expect(":");
                scanner.skipWhitespaces();

                rule.parseProperty(propName, this);

                scanner.skipWhitespaces();
                if (scanner.next != ';' && scanner.next != '}')
                    throw scanner.new CSSParseException("expected ';' or '}', but got " + chToString(scanner.next));

                scanner.expect(";");
                scanner.skipWhitespaces();
            }
            scanner.expect('}');
            scanner.skipWhitespaces();
        }
    }

    private Selector parseSelector() {
        List<Selector> selectors = new ArrayList<>();

        selectors.add(parseAncestorList());
        while (scanner.next == ',') {
            scanner.take();
            selectors.add(parseAncestorList());
        }

        if (selectors.isEmpty())
            throw scanner.new CSSParseException("empty selector list");

        if (selectors.size() == 1)
            return selectors.get(0);
        else
            return new Selector.DisjunctionSelector(selectors);
    }

    private Selector parseAncestorList() {
        scanner.skipWhitespaces();
        Selector selector = parseSiblingOperator();

        while (scanner.next != ',' && scanner.next != '{') {
            boolean direct;
            if (direct = (scanner.next == '>')) {
                scanner.take();
                scanner.skipWhitespaces();
            }
            Selector childSelector = parseSelector();
            selector = direct
                    ? new Selector.ChildSelector(selector, childSelector)
                    : new Selector.DescendantSelector(selector, childSelector);
            scanner.skipWhitespaces();
        }

        return selector;
    }

    private Selector parseSiblingOperator() {
        Selector s1 = parseConjunction();
        scanner.skipWhitespaces();
        if (scanner.tryRead('+'))
            return new Selector.SiblingSelector(s1, parseConjunction());
        else
            return s1;
    }

    private Selector parseConjunction() {
        List<Selector> selectors = new ArrayList<>();

        if (scanner.tryRead('*'))
            selectors.add(new Selector.ConjunctionSelector(List.of()));

        String elementName = scanner.tryReadIdentifier();
        if (elementName != null)
            selectors.add(new Selector.ElementSelector(elementName));

        loop:
        while (true) {
            if (Character.isWhitespace(scanner.next))
                break;
            switch (scanner.next) {
                case '{', ')', ',', '>', '+' -> {
                    break loop;
                }
                case '.' -> {
                    scanner.take();
                    selectors.add(new Selector.ClassSelector(scanner.readIdentifier()));
                }
                case ':' -> {
                    scanner.take();
                    if (scanner.tryRead(':')) { // pseudo-element
                        // TODO mi van ha a pseudoelemnév után következik még valami?
                        return new Selector.PseudoElementSelector(conjunction(selectors), scanner.readIdentifier());
                    } else
                        selectors.add(parsePseudoClass());
                }
                case '[' -> {
                    selectors.add(parseAttributeSelector());
                }
                default -> {
                    throw scanner.new CSSParseException("expected end of selector, '.', ':', whitespace or '[', but got " +
                            CSSScanner.chToString(scanner.next));
                }
            }
        }

        if (selectors.isEmpty())
            throw scanner.new CSSParseException("empty selector list");

        return conjunction(selectors);
    }

    private Selector conjunction(List<Selector> selectors) {
        if (selectors.size() == 1)
            return selectors.get(0);
        else
            return new Selector.ConjunctionSelector(selectors);
    }

    private Selector parsePseudoClass() {
        String name = scanner.readIdentifier();

        switch (name) {
            case "not" -> {
                scanner.expect('(');
                scanner.skipWhitespaces();
                Selector s = new Selector.NegateSelector(parseConjunction());
                scanner.expect(')');
                return s;
            }
            case "nth-child" -> {
                scanner.expect('(');
                Selector s = parseNthChild();
                scanner.skipWhitespaces();
                scanner.expect(')');
                return s;
            }
        }

        if (scanner.next == '(')
            throw scanner.new CSSParseException("function call '" + name + "' not supported");
//            int i1 = pos;
//            int n = 1;
//            while (pos < s.length()) {
//                if (c == '(') {
//                    n++;
//                } else if (c == ')')
//                    n--;
//                pos++;
//                if (n == 0)
//                    return new UnsupportedSelector(s.substring(begin, pos));
//            }
//            throw new IllegalArgumentException("Unbalanced parentheses around index " + i1 + ": " + s);


        return new Selector.PseudoClassSelector(name);
    }

    private Selector parseNthChild() {
        String keyword = scanner.tryReadIdentifier();
        if (keyword != null)
            return switch (keyword) {
                case "even" -> new Selector.NthChild(2, 0);
                case "odd" -> new Selector.NthChild(2, 1);
                default -> throw scanner.new CSSParseException("unknown nth-child value: " + keyword);
            };

        int param1 = scanner.readUnsignedInteger();
        scanner.skipWhitespaces();
        return switch (scanner.next) {
            case ')' -> new Selector.NthChild(0, param1);
            case 'n' -> {
                scanner.take();
                scanner.skipWhitespaces();
                yield switch (scanner.next) {
                    case '+' -> {
                        scanner.expect('+');
                        scanner.skipWhitespaces();
                        yield new Selector.NthChild(param1, scanner.readUnsignedInteger());
                    }
                    case ')' -> new Selector.NthChild(param1, 0);
                    default -> throw scanner.new CSSParseException("expected '+' or ')' but got " + chToString(scanner.next));
                };
            }
            default -> throw scanner.new CSSParseException("expected ')' or 'n' but got " + chToString(scanner.next));
        };
    }

    private Selector parseAttributeSelector() {
        throw scanner.new CSSParseException("attribute selector not supported");

//        int end = s.indexOf(']', pos) + 1;
//        Selector s = new UnsupportedSelector(this.s.substring(pos, end));
//        pos = end;
//        return s;
    }

    public BorderSpec parseBorder() {
        String a = scanner.tryReadIdentifier();
        if (a != null)
            if (a.equals("none"))
                return new BorderSpec(Length.zero(), new Fill.ColorFill(Colors.TRANSPARENT));
            else
                throw scanner.new CSSParseException("unexpected token: " + a);

        Length len = parseLength();
        scanner.skipWhitespaces();
        scanner.expectIdentifier("solid");
        scanner.skipWhitespaces();
        Fill fill = parseFill();
        return new BorderSpec(len, fill);
    }


    public Fill parseFill() {
        if (scanner.next == '#') {
            return new Fill.ColorFill(parseColor());
        }

        String id = scanner.readIdentifier();
        return switch (id) {
            case "linear-gradient" -> parseLinearGradient();
            default -> throw scanner.new CSSParseException("unknown id: " + id);
        };
    }

    public Color parseColor() {
        scanner.skipWhitespaces();
        scanner.expect('#');
        String s = scanner.readAlphanumericWord();
        return switch (s.length()) {
            case 3 -> RGBColor.ofRGBShort(Integer.parseInt(s, 16));
            case 6 -> RGBColor.ofRGB(Integer.parseInt(s, 16));
            case 8 -> RGBColor.ofIntRGBA(Integer.parseUnsignedInt(s, 16));
            default -> throw scanner.new CSSParseException("unknown fill: #" + s);
        };
    }

    public Fill.LinearGradientFill parseLinearGradient() {
        scanner.skipWhitespaces();
        scanner.expect("(");

        PointSpec from, to;

        String s = scanner.skipWhitespaceAndReadIdentifier();
        switch (s) {
            case "from" -> {
                from = parsePoint();
                scanner.expectAndSkipWhitespaces();
                scanner.expectIdentifier("to");
                scanner.skipWhitespaces();
                to = parsePoint();
            }
            case "to" -> {
                String s2 = scanner.skipWhitespaceAndReadIdentifier();
                switch (s2) {
                    case "top" -> {
                        from = leftBottom();
                        to = leftTop();
                    }
                    case "right" -> {
                        from = leftTop();
                        to = rightTop();
                    }
                    case "bottom" -> {
                        from = leftTop();
                        to = leftBottom();
                    }
                    case "left" -> {
                        from = rightTop();
                        to = leftTop();
                    }
                    default -> throw scanner.new CSSParseException(
                            "unknown linear gradient endpoint specification: 'to " + s2 + "'");
                }
            }
            default -> throw scanner.new CSSParseException("expected 'from' or 'to', but got '" + s + "'");
        }

        List<Fill.LinearGradientFill.Stop> stops = new ArrayList<>();

        scanner.skipWhitespaces();
        while (scanner.next == ',') {
            scanner.expect(',');
            stops.add(readGradientStop(stops.isEmpty()));
            scanner.skipWhitespaces();
        }
        scanner.expect(')');

        return new Fill.LinearGradientFill(from, to, stops);
    }

    public Fill.LinearGradientFill.Stop readGradientStop(boolean first) {
        Color color = parseColor();
        scanner.skipWhitespaces();

        if (scanner.next == ')')
            return new Fill.LinearGradientFill.Stop(color, percent(100));

        if (scanner.next == ',')
            if (first)
                return new Fill.LinearGradientFill.Stop(color, zero());
            else
                throw scanner.new CSSParseException("no fraction provided for gradient stop and not first or last");

        return new Fill.LinearGradientFill.Stop(color, parseLength());
    }

    private static PointSpec leftTop() {
        return new PointSpec(zero(), zero());
    }

    private static PointSpec leftBottom() {
        return new PointSpec(zero(), percent(100));
    }

    private static PointSpec rightTop() {
        return new PointSpec(percent(100), zero());
    }

    public PointSpec parsePoint() {
        scanner.skipWhitespaces();
        Length x = parseLength();
        scanner.expectAndSkipWhitespaces();
        Length y = parseLength();
        return new PointSpec(x, y);
    }

    public Length parseLength() {
        NumberWithUnit n = parseNumberWithUnit();
        return switch (n.unit) {
            case NULL -> {
                if (n.n == 0)
                    yield Length.zero();
                else
                    throw scanner.new CSSParseException("expected length unit");
            }
            case PX -> new Length(n.val(), 0, 0);
            case PERCENT -> new Length(0, 0, n.val());
            case EM -> new Length(0, n.val(), 0);
        };
    }

    public NumberWithUnit parseNumberWithUnit() {
        String l = scanner.readPossibleChars("0123456789-.");
        double d = Double.parseDouble(l);
        String unitString = scanner.readPossibleCharsOrEmpty("px%em");
        Unit unit = switch (unitString) {
            case "px" -> Unit.PX;
            case "%" -> Unit.PERCENT;
            case "em" -> Unit.EM;
            case "" -> Unit.NULL;
            default -> throw scanner.new CSSParseException("unknown length unit: '" + unitString + "'");
        };
        return new CSSParser.NumberWithUnit(d, unit);
    }

    public List<Length> parseLengths() {
        scanner.skipWhitespaces();

        List<Length> lengths = new ArrayList<>();
        do {
            lengths.add(parseLength());
            scanner.skipWhitespaces();
        } while (scanner.next != ';');

        return lengths;
    }

    public List<TransitionSpec<?>> parseTransitionList() {
        List<TransitionSpec<?>> transitions = new ArrayList<>();
        scanner.skipWhitespaces();
        do {
            String propName = scanner.readIdentifier();
            scanner.skipWhitespaces();
            Duration duration = parseDuration();
            transitions.add(new TransitionSpec<>(CSSProperty.ofName(propName), duration));
            scanner.skipWhitespaces();
        } while (scanner.next == ',');
        return transitions;
    }

    private Duration parseDuration() {
        String s = scanner.readAlphanumericWord();
        if (s.endsWith("ms"))
            return Duration.ofMillis(Integer.parseInt(s.substring(0, s.length() - "ms".length())));
        else if (s.endsWith("s"))
            return Duration.ofSeconds(Integer.parseInt(s.substring(0, s.length() - "s".length())));
        else
            throw scanner.new CSSParseException("unknown duration unit: " + s);
    }

    public TextAlign parseTextAlign() {
        String s = scanner.readIdentifier();
        return switch (s) {
            case "left" -> TextAlign.LEFT;
            case "center" -> TextAlign.CENTER;
            case "right" -> TextAlign.RIGHT;
            default -> throw scanner.new CSSParseException("unknown text align value: " + s);
        };
    }

    public FontWeight parseFontWeight() {
        String s = scanner.readIdentifier();
        return switch (s) {
            case "normal" -> FontWeight.NORMAL;
            case "bold" -> FontWeight.BOLD;
            default -> throw scanner.new CSSParseException("unknown font weight value: " + s);
        };
    }

    public Fraction parseNumberAsFraction() {
        // TODO eliminate doubles
        String l = scanner.readPossibleChars("0123456789-.");
        double d = Double.parseDouble(l);
        return Fraction.of(d, (int) Math.pow(10, l.length()));
    }

    public record NumberWithUnit(double n, Unit unit) {

        public NumberWithUnit {
            if (!Double.isFinite(n))
                throw new IllegalArgumentException(n + " " + unit);
        }

        public int val() {
            return (int) Math.round(n * 16384);
        }
    }

}
