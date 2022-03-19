package ui10.layout;

import ui10.base.Element;
import ui10.base.LayoutContext1;
import ui10.binding2.Property;
import ui10.geom.*;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Grid extends RectangularLayout {

    public static final Property<Integer> GAP_PROPERTY = new Property<>();

    public final List<? extends List<? extends Element>> rows;

    private int gap = 0;

    public Grid(List<? extends List<? extends Element>> rows) {
        this.rows = rows;
    }

    @Override
    public void initFromProps() {
        Integer i = getProperty(GAP_PROPERTY);
        gap = i == null ? 0 : i;
    }

    @Override
    public void enumerateStaticChildren(Consumer<Element> consumer) {
        rows.forEach(row -> row.forEach(consumer));
    }

    @Override
    protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        return computeLayout(constraints, context).containerSize;
    }

    @Override
    protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
        GridLayout layout = computeLayout(BoxConstraints.fixed(size), context);

        int y = 0;
        for (int rowNumber = 0; rowNumber < rows.size(); rowNumber++) {
            List<? extends Element> row = rows.get(rowNumber);
            int rowHeight = layout.rows[rowNumber];
            int x = 0;
            for (int col = 0; col < row.size(); col++) {
                int colWidth = layout.cols[col];
                placer.accept(row.get(col), new Rectangle(new Point(x, y), new Size(colWidth, rowHeight)));
                x += colWidth + gap;
            }
            y += rowHeight + gap;
        }
    }

    private GridLayout computeLayout(BoxConstraints constraints, LayoutContext1 context) {
        List<Column> cols = IntStream.range(0, rows.get(0).size()).mapToObj(i -> new Column(i, context)).toList();

        FlexLayout l = new FlexLayout(Axis.HORIZONTAL, constraints, cols);
        l.gap = gap;
        l.layout();

        int width = l.containerSize.width();

        int[] colWidths = l.childrenSizes.stream().mapToInt(Size::width).toArray();

        List<Row> rows = this.rows.stream().map(r -> new Row(colWidths, r, context)).toList();
        l = new FlexLayout(Axis.VERTICAL, constraints, rows);
        l.gap = gap;
        l.layout();

        int height = l.containerSize.height();

        int[] rowHeights = l.childrenSizes.stream().mapToInt(Size::height).toArray();
        return new GridLayout(colWidths, rowHeights, new Size(width, height));
    }

    @Override
    public String elementName() {
        return null; // not a real control, only a layout
    }

    private class Column implements FlexLayout.FlexElement {

        private final int colIndex;
        private final LayoutContext1 context;

        public Column(int colIndex, LayoutContext1 context) {
            this.colIndex = colIndex;
            this.context = context;
        }

        @Override
        public Size preferredSize(BoxConstraints constraints) {
            constraints = constraints.withUnboundedHeight();

            int w = 0;
            for (List<? extends Element> row : rows) {
                Element e = row.get(colIndex);
                Size size = context.preferredSize(e, constraints);
                w = Math.max(w, size.width());
            }

            return new Size(w, 1);
        }

        @Override
        public Fraction growFactor() {
            return LinearLayout.GROW_FACTOR.defaultValue;
        }
    }

    private class Row implements FlexLayout.FlexElement {

        private final int[] colWidths;
        private final List<? extends Element> row;
        private final LayoutContext1 context;

        public Row(int[] colWidths, List<? extends Element> row, LayoutContext1 context) {
            this.colWidths = colWidths;
            this.row = row;
            this.context = context;
        }

        @Override
        public Size preferredSize(BoxConstraints constraints) {
            int h = 0;
            for (int colIndex = 0; colIndex < colWidths.length; colIndex++) {
                Element e = row.get(colIndex);
                Size size = context.preferredSize(e, constraints.withWidth(colWidths[colIndex], colWidths[colIndex]));
                h = Math.max(h, size.height());
            }

            return new Size(constraints.min().width(), h);
        }

        @Override
        public Fraction growFactor() {
            return LinearLayout.GROW_FACTOR.defaultValue;
        }
    }

    private record GridLayout(int[] cols, int[] rows, Size containerSize) {
    }
}
