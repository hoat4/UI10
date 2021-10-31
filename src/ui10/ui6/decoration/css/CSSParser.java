package ui10.ui6.decoration.css;


import ui10.image.Color;
import ui10.image.RGBColor;
import ui10.ui6.decoration.BorderSpec;
import ui10.ui6.decoration.Fill;
import ui10.ui6.decoration.PointSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ui10.ui6.decoration.css.CSSScanner.chToString;
import static ui10.ui6.decoration.css.Length.percent;
import static ui10.ui6.decoration.css.Length.zero;

public class CSSParser {

    private final CSSScanner scanner;
    public final Map<String, Rule> rulesByClass = new HashMap<>();

    private final Map<String, String> vars= new HashMap<>();

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

            scanner.expect(".");
            String className = scanner.readIdentifier();
            scanner.skipWhitespaces();
            scanner.expect("{");

            Rule rule = new Rule();
            rulesByClass.put(className, rule);

            scanner.skipWhitespaces();
            while (scanner.next != '}') {
                var propName = scanner.readIdentifier();
                scanner.skipWhitespaces();
                scanner.expect(":");
                scanner.skipWhitespaces();

                rule.parseProperty(propName, this);

                scanner.skipWhitespaces();
                if (scanner.next != ';' && scanner.next != '}')
                    throw new CSSScanner.CSSParseException("expected ';' or '}', but got " + chToString(scanner.next));

                scanner.expect(";");
                scanner.skipWhitespaces();
            }
            scanner.expect('}');
            scanner.skipWhitespaces();
        }
    }

    public BorderSpec parseBorder() {
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
            default -> throw new CSSScanner.CSSParseException("unknown id: " + id);
        };
    }

    public Color parseColor() {
        scanner.skipWhitespaces();
        scanner.expect('#');
        String s = scanner.readIdentifier();
        return switch (s.length()) {
            case 3 -> RGBColor.ofRGBShort(Integer.parseInt(s, 16));
            case 6 -> RGBColor.ofRGB(Integer.parseInt(s, 16));
            case 8 -> RGBColor.ofIntRGBA(Integer.parseInt(s, 16));
            default -> throw new CSSScanner.CSSParseException("unknown fill: #" + s);
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
                    default -> throw new CSSScanner.CSSParseException(
                            "unknown linear gradient endpoint specification: 'to " + s2 + "'");
                }
            }
            default -> throw new CSSScanner.CSSParseException("expected 'from' or 'to', but got '" + s + "'");
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
                throw new CSSScanner.CSSParseException("no fraction provided for gradient stop and not first or last");

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
            case NULL -> throw new CSSScanner.CSSParseException("expected length unit");
            case PX -> new Length(n.val(), 0, 0);
            case PERCENT -> new Length(0, 0, n.val());
        };
    }

    public NumberWithUnit parseNumberWithUnit() {
        String l = scanner.readPossibleChars("0123456789-.");
        double d = Double.parseDouble(l);
        String unitString = scanner.readPossibleCharsOrEmpty("px%");
        Unit unit = switch (unitString) {
            case "px" -> Unit.PX;
            case "%" -> Unit.PERCENT;
            case "" -> Unit.NULL;
            default -> throw new CSSScanner.CSSParseException("unknown length unit: '" + unitString + "'");
        };
        return new CSSParser.NumberWithUnit(d, unit);
    }

    public record NumberWithUnit(double n, Unit unit) {

        public NumberWithUnit {
            if (!Double.isFinite(n))
                throw new IllegalArgumentException(n+" "+unit);
        }

        public int val() {
            return (int) Math.round(n * 16384);
        }
    }

}