package ui10.ui6.decoration;


import ui10.decoration.css.CSS;
import ui10.image.Color;
import ui10.image.Fill;
import ui10.image.RGBColor;
import ui10.nodes.Border;

import java.util.HashMap;
import java.util.Map;

import static ui10.ui6.decoration.CSSScanner.chToString;

public class CSSParser {

    private final CSSScanner scanner;
    final Map<String, Rule> rulesByClass = new HashMap<>();

    public CSSParser(CSSScanner scanner) {
        this.scanner = scanner;
    }

    public void parseCSS() {
        scanner.skipWhitespaces();

        while (scanner.next != -1) {
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
        Color color = parseColor();
        return new BorderSpec(len, color);
    }

    public Color parseColor() {
        scanner.expect('#');
        String s = scanner.readIdentifier();
        return switch (s.length()) {
            case 3 -> RGBColor.ofRGBShort(Integer.parseInt(s, 16));
            case 6 -> RGBColor.ofRGB(Integer.parseInt(s, 16));
            case 8 -> RGBColor.ofIntRGBA(Integer.parseInt(s, 16));
            default -> throw new CSSScanner.CSSParseException("unknown color: #" + s);
        };
    }

    public Length parseLength() {
        NumberWithUnit n = parseNumberWithUnit();
        switch (n.unit) {
            case NULL:
                throw new CSSScanner.CSSParseException("expected length unit");
            case PX:
                return new Length(n.val(), 0, 0);
            case PERCENT:
                return new Length(0, 0, n.val());
            default:
                throw new UnsupportedOperationException("unknown length unit: " + n.unit);
        }
    }

    public NumberWithUnit parseNumberWithUnit() {
        String l = scanner.readPossibleChars("0123456789-.");
        double d = Double.parseDouble(l);
        String unitString = scanner.readPossibleCharsOrEmpty("px%");
        CSSParser.Unit unit = switch (unitString) {
            case "px" -> CSSParser.Unit.PX;
            case "%" -> CSSParser.Unit.PERCENT;
            case "" -> CSSParser.Unit.NULL;
            default -> throw new CSSScanner.CSSParseException("unknown length unit: '" + unitString + "'");
        };
        return new CSSParser.NumberWithUnit(d, unit);
    }

    public record NumberWithUnit(double n, CSSParser.Unit unit) {

        public NumberWithUnit {
            if (!Double.isFinite(n))
                throw new IllegalArgumentException(n+" "+unit);
        }

        public int val() {
            return (int) Math.round(n * 16384);
        }
    }

    public enum Unit {NULL, PX, PERCENT}

}
