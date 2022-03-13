package ui10.shell.renderer.sw;

import jdk.incubator.foreign.MemoryAccess;
import ui10.base.LayoutContext1;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

import java.io.IOException;
import java.io.InputStream;

public class FontTest extends SWRenderableElement {

    private final byte[] font;
    private int posInFontFile;

    public FontTest() {
        try (InputStream in = getClass().getResourceAsStream("font.txt")) {
            this.font = in.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context1) {
        return new Size(256, 256);
    }

    @Override
    protected void draw(SWRasterizer g, Rectangle rectangle) {
        posInFontFile = 0;
        int[][] polynomials = new int[readU1()][];
        for (int i = 0; i < polynomials.length; i++) {
            int degree = readU1();
            polynomials[i] = new int[degree + 1];
            for (int j = 0; j <= degree; j++)
                polynomials[i][j] = j == 0 ? readS1() : readS2();
        }

        for (int y = 0; y < rectangle.height(); ) {
            System.out.println(y);
            int b = readU1();
            if ((b & 128) != 0) {
                y += b & ~128;
                continue;
            }

            for (int segmentMax = y + b, begin = posInFontFile; y < segmentMax; y++) {
                while (true) {
                    int leftPolynomialNumber = readU1(), rightPolynomialNumber = readU1();
                    int[] leftPolynomial = polynomials[leftPolynomialNumber];
                    int[] rightPolynomial = polynomials[rightPolynomialNumber & 127];

                    long left = leftPolynomial[0];
                    for (long i = 1, y2 = y; i < leftPolynomial.length; i++) {
                        left += leftPolynomial[(int) i] * y2 >> 16;
                        y2 *= y;
                    }

                    long right = rightPolynomial[0];
                    for (long i = 1, y2 = y; i < rightPolynomial.length; i++) {
                        right += rightPolynomial[(int) i] * y2 >> 16;
                        y2 *= y;
                    }

                    System.out.println(y + ": " + left + ", " + right);

                    // MemoryAccess.setIntAtIndex(g.buffer, g.coord(new Point(rectangle.left(),rectangle.top()+y)), 0xFFFF0000);
                    int leftI = (int)left, rightI = (int)right;
                    for (int pos = g.coord(new Point(rectangle.left() + leftI, rectangle.top() + y)),
                         end = pos + rightI - leftI; pos < end; pos++) {

                        MemoryAccess.setIntAtIndex(g.buffer, pos, 0xFF000000);
                    }

                    if ((rightPolynomialNumber & 128) != 0)
                        break;
                }
                if (y != segmentMax - 1)
                    posInFontFile = begin;
            }
        }
    }

    private int readU1() {
        return readInt();
    }

    private int readS1() {
        return readInt();
    }

    private int readS2() {
        return readInt();
    }

    private int readInt() {
        int sign = skipNonDigits() ? -1 : 1;

        int i = 0;
        while (true) {
            int ch = font[posInFontFile++];
            if (ch >= '0' && ch <= '9') {
                i *= 10;
                i += ch - '0';
            } else {
                return i * sign;
            }
        }
    }

    private boolean skipNonDigits() {
        int ch;
        while ((ch = font[posInFontFile]) < '0' || ch > '9') {
            if (ch == '-') {
                posInFontFile++;
                return true;
            }
            else if (ch == '(')
                do {
                    posInFontFile++;
                } while (font[posInFontFile] != ')');
            posInFontFile++;
        }
        return false;
    }

}
