package ui10.decoration.css;

import ui10.decoration.css.CSS.I;
import ui10.geom.Point;
import ui10.image.Color;
import ui10.image.Fill;
import ui10.image.LinearGradient;
import ui10.image.RGBColor;
import ui10.nodes.Border;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static ui10.decoration.css.CSSScanner.chToString;

public class CSSParser {

    private final CSSScanner scanner;
    private final Visitor ruleVisitor;

    public CSSParser(CSSScanner scanner, Visitor ruleVisitor) {
        this.scanner = scanner;
        this.ruleVisitor = ruleVisitor;
    }


    public void parseCSS() {
        scanner.skipWhitespaces();

        while (scanner.next != -1) {
            scanner.expect(".");
            String className = scanner.readIdentifier();
            scanner.skipWhitespaces();
            scanner.expect("{");
            ruleVisitor.beginRule(className);

            scanner.skipWhitespaces();
            while (scanner.next != '}') {
                var propName = scanner.readIdentifier();
                scanner.skipWhitespaces();
                scanner.expect(":");
                scanner.skipWhitespaces();

                ruleVisitor.property(propName, this);

                scanner.skipWhitespaces();
                if (scanner.next != ';' && scanner.next != '}')
                    throw new CSSScanner.CSSParseException("expected ';' or '}', but got " + chToString(scanner.next));

                scanner.expect(";");
            }
            ruleVisitor.endRule();

            scanner.skipWhitespaces();
        }
    }

    public interface Visitor {

        void beginRule(String className);

        void property(String name, CSSParser p);

        void endRule();
    }

    public I<Border.BorderStyle> parseBorder() {
        I<Integer> len = parseLength(null);
        scanner.skipWhitespaces();
        scanner.expectIdentifier("solid");
        scanner.skipWhitespaces();
        I<Fill> fill = parseFill();
        return uv -> new Border.BorderStyle(len.value(uv), fill.value(uv), 0);
    }

    public I<Fill> parseFill() {
        if (scanner.next == '#') {
            return u -> parseColor();
        }

        String id = scanner.readIdentifier();
        return switch (id) {
            case "linear-gradient" -> parseLinearGradient();
            default -> throw new CSSScanner.CSSParseException("unknown id: " + id);
        };
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

    public I<Fill> parseLinearGradient() {
        scanner.skipWhitespaces();
        scanner.expect("(");

        I<Point> from, to;

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

        List<I<LinearGradient.Stop>> stops = new ArrayList<>();

        scanner.skipWhitespaces();
        while (scanner.next == ',') {
            stops.add(readGradientStop(stops.isEmpty(), uv -> Point.distance(from.value(uv), to.value(uv))));
            scanner.skipWhitespaces();
        }
        scanner.expect(')');

        return uv -> new LinearGradient(from.value(uv), to.value(uv),
                stops.stream().map(u -> u.value(uv)).toList());
    }

    public I<LinearGradient.Stop> readGradientStop(boolean first, I<Integer> lineLength) {
        Color color = parseColor();
        scanner.skipWhitespaces();

        if (scanner.next == ')')
            return uv -> new LinearGradient.Stop(color, 1);

        if (scanner.next == ',')
            if (first)
                return uv -> new LinearGradient.Stop(color, 0);
            else
                throw new CSSScanner.CSSParseException("no fraction provided for gradient stop and not first or last");

        NumberWithUnit f = parseNumberWithUnit();
        I<Double> fraction = switch (f.unit) {
            case NULL -> uv -> f.n; // ???
            case PERCENT -> uv -> f.n / 100;
            case PX -> uv -> uv.px(f.n) / ((double) lineLength.value(uv));
        };

        return uv -> new LinearGradient.Stop(color, fraction.value(uv));
    }

    private static I<Point> leftTop() {
        return u -> Point.ORIGO;
    }

    private static I<Point> leftBottom() {
        return u -> new Point(0, u.heightPercent(100));
    }

    private static I<Point> rightTop() {
        return u -> new Point(u.widthPercent(100), 0);
    }

    public I<Point> parsePoint() {
        I<Integer> x = parseLength(UnitValues::widthPercent);
        scanner.expectAndSkipWhitespaces();
        I<Integer> y = parseLength(UnitValues::heightPercent);
        return u -> new Point(x.value(u), y.value(u));
    }

    public I<Integer> parseLength(BiFunction<UnitValues, Double, Integer> percentageProvider) {
        NumberWithUnit u = parseNumberWithUnit();
        if (u.unit == Unit.NULL && u.n != 0)
            throw new CSSScanner.CSSParseException("no unit specified but numeric value is non-zero: " + u.n);
        return switch (u.unit) {
            case NULL -> uv -> 0;
            case PX -> uv -> uv.px(u.n);
            case PERCENT -> {
                if (percentageProvider == null)
                    throw new CSSScanner.CSSParseException("percentage not supported here");
                else
                    yield uv -> percentageProvider.apply(uv, u.n);
            }
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
        return new NumberWithUnit(d, unit);
    }

    public record NumberWithUnit(double n, Unit unit) {
    }

    public enum Unit {NULL, PX, PERCENT}

    enum Direction {
        HORIZONTAL, VERTICAL
    }

}
