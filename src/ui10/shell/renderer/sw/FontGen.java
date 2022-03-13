package ui10.shell.renderer.sw;

import ui10.geom.Point;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class FontGen {

    private final List<InternedPolynomial> polynomials = new ArrayList<>();
    private List<Segment> prevRow, thisRow;
    private final List<Section> sections = new ArrayList<>();
    private Section currentSection;

    private void processSegments(List<Segment> segments) {
        int begin = 0;
        for (int i = 1; i <= segments.size(); i++) {
            if (i == segments.size() || segments.get(i).y != segments.get(begin).y) {
                prevRow = thisRow;
                thisRow = segments.subList(begin, i);
                begin = i;
                if (!thisRow.isEmpty()) // utolsó iterációnál lehet üres
                    processRow();
            }
        }

        for (Section section : sections)
            section.curves2 = section.curves.stream().map(p -> internPolynomial(makePolynomial(p))).toList();

        System.out.println(polynomials.size());
        for (int i = 0; i < polynomials.size(); i++) {
            System.out.print("(p" + i + ") ");
            polynomials.get(i).print();
        }
        System.out.println();

        int y = 0;
        for (Section section : sections) {
            if (section.y != y) {
                printWhiteLines(section.y - y);
            }
            System.out.print("(y=" + section.y + ") " + section.height + ":");
            for (int i = 0; i < section.curves2.size(); i += 2) {
                System.out.print(" " + polynomials.indexOf(section.curves2.get(i)));
                int rightP = polynomials.indexOf(section.curves2.get(i + 1));
                if (i == section.curves2.size() - 2)
                    rightP += 128;
                System.out.print("-" + rightP);
            }
            System.out.println();
            y = section.y + section.height;
        }
        printWhiteLines(256 - y);
    }

    private InternedPolynomial internPolynomial(Polynomial p) {
        List<Integer> ints = new ArrayList<>();
        ints.add((int) Math.round(p.coefficients[0]));
        for (int i = 1; i < p.coefficients.length; i++)
            ints.add((int) Math.round((1 << 24) * p.coefficients[i]));
        InternedPolynomial i = new InternedPolynomial(ints);
        if (!polynomials.contains(i))
            polynomials.add(i);
        return i;
    }

    private Polynomial makePolynomial(List<Point> values) {
        values = values.stream().map(p -> new Point(p.y(), p.x())).toList();

        if (values.size() == 1)
            return new Polynomial(values.get(0).y());

        //System.out.println(makeP3(List.of(values.get(0), values.get(values.size() - 1))));
        //Polynomial p = makeP2(values);
        int maxPts = 3;
        if (values.size() > maxPts) {
            List<Point> l = values;
            values = IntStream.range(0, maxPts).mapToObj(i -> l.get((int) Math.round(i * l.size() / (double) maxPts))).toList();
        }
        Polynomial p = makePolynomialImpl(values);
        System.out.println(values + " -> " + p);
        return p;
    }

    private static Polynomial makePolynomialImpl(List<Point> points) {
        // Gauss-elimination

        int n = points.size();
        double[][] matrix = new double[n][n + 1]; // row-first
        for (int i = 0; i < n; i++) {
            Point point = points.get(i);
            for (int j = 0; j < n; j++) {
                matrix[i][j] = Math.pow(point.x(), j);
            }
            matrix[i][n] = point.y();
        }
        //System.out.println();
        //System.out.println("step 1: " + Arrays.deepToString(matrix));
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++)
                mulSub(matrix, i, j);
        //System.out.println("step 2: " + Arrays.deepToString(matrix));
        double[] p = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            p[i] = matrix[i][n] / matrix[i][i];
            //  System.out.println("step 2/" + i + ": " + Arrays.deepToString(matrix));
            for (int j = 0; j < i; j++)
                mulSub(matrix, i, j);
        }
        //System.out.println("step 3: " + Arrays.deepToString(matrix));
        return new Polynomial(p);
    }

    private static void mulSub(double[][] matrix, int i, int j) { // m[j] -= m[i] * m[j][i] / m[i][i]
        double q = matrix[j][i] / matrix[i][i];
        for (int k = 0; k < matrix[i].length; k++) {
            matrix[j][k] -= matrix[i][k] * q;
        }
    }

    private Polynomial makeP2(List<Point> values) {
        double x1 = values.get(0).x(), y1 = values.get(0).y();
        double x2 = values.get(values.size() - 1).x(), y2 = values.get(values.size() - 1).y();
        double m = (y1 - y2) / (x1 - x2);
        return new Polynomial(y1 - m * x1, m);
    }

    private void processRow() {
        if (prevRow == null || thisRow.size() != prevRow.size()) {
            beginSection();
            return;
        }

        for (Segment s : thisRow) {
            List<Segment> origins = prevRow.stream().filter(sp -> intersectsHorizontally(s, sp)).toList();
            if (origins.size() != 1) {
                beginSection();
                return;
            }
        }

        for (int i = 0; i < thisRow.size(); i++) {
            Segment segment = thisRow.get(i);
            currentSection.curves.get(i * 2).add(segment.leftPoint());
            currentSection.curves.get(i * 2 + 1).add(segment.rightPoint());
        }
        currentSection.height++;
    }

    private boolean intersectsHorizontally(Segment a, Segment b) {
        return b.x1 <= a.x2 && b.x2 >= a.x1;
    }

    private void beginSection() {
        currentSection = new Section();
        sections.add(currentSection);

        currentSection.y = thisRow.get(0).y;
        currentSection.height = 1;
        currentSection.curves = new ArrayList<>();
        for (Segment s : thisRow) {
            currentSection.curves.add(new ArrayList<>(List.of(s.leftPoint())));
            currentSection.curves.add(new ArrayList<>(List.of(s.rightPoint())));
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = img.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, img.getWidth(), img.getHeight());
            g.setFont(new Font(Font.DIALOG, Font.PLAIN, 100));
            g.setColor(Color.BLACK);
            g.drawString("J", 10, g.getFontMetrics().getAscent() + 10);
        } finally {
            g.dispose();
        }
        ImageIO.write(img, "png", new File("a.png"));

        List<Segment> segments = new ArrayList<>();
        for (int y = 0; y < img.getHeight(); y++) {
            boolean prev = false;
            int begin = -1;
            for (int x = 0; x < img.getWidth(); x++) {
                boolean b = img.getRGB(x, y) != 0xFFFFFFFF;
                if (b && !prev) {
                    begin = x;
                } else if (!b && prev) {
                    segments.add(new Segment(y, begin, x));
                }
                prev = b;
            }
        }

        new FontGen().processSegments(segments);

//        List<Integer> cols = segments.stream().flatMap(t -> Stream.of(t.x1, t.x2)).
//                distinct().sorted().collect(Collectors.toList());
//        System.out.println(cols.size());
//        for (Integer col : cols)
//            System.out.println("deg0 " + col);
//        System.out.println();
//
//        int prevY = -1;
//        int i = 0;
//        for (Segment segment : segments) {
//            printWhiteLines(segment.y - prevY - 1);
//            if (segment.y != prevY)
//                System.out.print("1:");
//
//            boolean lastInLine = i == segments.size() - 1 || segments.get(i + 1).y != segment.y;
//            System.out.print(" " + cols.indexOf(segment.x1) + "-" + (cols.indexOf(segment.x2) + (lastInLine ? 128 : 0)));
//            if (lastInLine)
//                System.out.println();
//
//            i++;
//            prevY = segment.y;
//        }
//        printWhiteLines(img.getHeight() - prevY);
    }

    private static void printWhiteLines(int rem) {
        while (rem > 127) {
            System.out.println("255 (127)");
            rem -= 127;
        }
        if (rem > 0)
            System.out.println((rem | 128) + " (" + rem + ")");
    }

    private record Segment(int y, int x1, int x2) {
        public Point leftPoint() {
            return new Point(x1, y);
        }

        public Point rightPoint() {
            return new Point(x2, y);
        }
    }

    private record Polynomial(double... coefficients) {

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < coefficients.length; i++) {
                if (i != 0)
                    sb.append(" + ");
                sb.append(String.format(Locale.ENGLISH, "%.3f", coefficients[i]));
                sb.append(switch (i) {
                    case 0 -> "";
                    case 1 -> "x";
                    default -> "x^" + i;
                });
            }
            return sb.toString();
        }
    }

    private record InternedPolynomial(List<Integer> coefficients) {

        void print() {
            System.out.print("deg" + (coefficients.size() - 1));
            coefficients.forEach(c -> System.out.print(" " + c));
            System.out.println();
        }
    }


    private static class Section {
        int y, height;
        List<List<Point>> curves;
        List<InternedPolynomial> curves2;
    }
}
