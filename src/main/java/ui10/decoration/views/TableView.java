package ui10.decoration.views;

import ui10.base.Element;
import ui10.base.LayoutContext1;
import ui10.controls.Table;
import ui10.controls.TextView;
import ui10.decoration.StyleableContainer;
import ui10.decoration.css.CSSProperty;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;
import ui10.layout.Layouts;
import ui10.layout.LinearLayoutBuilder;
import ui10.layout.RectangularLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@ElementName("Table")
public class TableView<T> extends StyleableView<Table<T>> {

    private final TableHeader header = new TableHeader();

    public TableView(Table<T> model) {
        super(model);
    }

    @Override
    protected Element contentImpl() {
        return LinearLayoutBuilder.vertical().
                add(0, header).
                add(1, new TableBody()).
                build();
    }

    private List<Integer> colWidths(int containerWidth) {
        // TODO handle empty col list

        List<Integer> colWidths = new ArrayList<>();
        int availWidth = containerWidth - (model.columns.size() - 1) * separatorThickness();
        for (int i = 0; i < model.columns.size(); i++) {
            int w = availWidth / (model.columns.size() - i);
            colWidths.add(w);
            availWidth -= w;
        }
        return colWidths;
    }

    private int separatorThickness() {
        return 1;
    }

    private int rowHeight() {
        return decorContext.length(rule.get(CSSProperty.rowHeight));
    }

    @ElementName("TableHeader")
    public class TableHeader extends StyleableContainer {

        private final List<TableColumnHeader> colHeaders = new ArrayList<>();

        @RepeatedInit
        void makeColHeaders() {
            colHeaders.clear();

//            List<Integer> colWidths = colWidths(getShapeOrFail().bounds().width());

            int x = 0;
            for (int i = 0; i < model.columns.size(); i++) {
                Table.Column<T> col = model.columns.get(i);
                colHeaders.add(new TableColumnHeader(col));

                //              int w = colWidths.get(i);
                //            x += w;
                if (i != model.columns.size() - 1) {
                    x += TableView.this.separatorThickness();

                    // dragger
                }
            }
        }

        private Element makeCellSeparator() {
            return rule.get(CSSProperty.cellSeparator).makeElement(decorContext);
        }


        @Override
        protected Element contentImpl() {
            return new RowLayout(colHeaders, this::makeCellSeparator);
        }
    }

    @ClassName("table-column-header")
    public class TableColumnHeader extends TextView {

        private final Table.Column<?> column;

        public TableColumnHeader(Table.Column<?> column) {
            this.column = column;
        }

        @RepeatedInit
        void setupText() {
            text(column.caption);
        }
    }

    @ElementName("TableBody")
    public class TableBody extends StyleableContainer {

        @Override
        protected Element contentImpl() {
            return new TableBodyImpl();
        }

        private class TableBodyImpl extends RectangularLayout {


            @Override
            public void enumerateChildren(Consumer<Element> consumer) {
            }


            @Override
            protected Size preferredSize(BoxConstraints constraints, LayoutContext1 context1) {
                return constraints.min();
            }

            @Override
            protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
                int rowHeight = TableView.this.rowHeight();
                int separatorThickness = TableView.this.separatorThickness();

                for (int i = 1, y = 0; y < size.height(); y += rowHeight + separatorThickness) {
                    TableRow row = new TableRow();
                    row.index = i - 1;
                    i++;

                    // row.setProperty(IndexInSiblings.INDEX_IN_SIBLINGS_PROPERTY, i++);
                    // ???

                    placer.accept(row, new Rectangle(0, y, size.width(), rowHeight));

                    placer.accept(makeCellSeparator(),
                            new Rectangle(0, y + rowHeight, size.width(), separatorThickness));
                }
            }

            private Element makeCellSeparator() {
                return rule.get(CSSProperty.cellSeparator).makeElement(decorContext);
            }

            @ElementName("TableRow")
            public class TableRow extends StyleableContainer {

                int index;

                @Override
                protected Element contentImpl() {
                    if (model.data.size() <= index) {
                        // kellenek cellseparatorok is ilyenkor
                        return new RowLayout(IntStream.range(0, model.columns.size()).
                                mapToObj(i -> Layouts.empty()).toList(), TableBodyImpl.this::makeCellSeparator);
                    }

                    T obj = model.data.get(index);

                    List<Element> cells = new ArrayList<>();
                    for (Table.Column<T> col : model.columns) {
                        cells.add(new DefaultTableCell(col.getter.apply(obj)));
                    }
                    return new RowLayout(cells, TableBodyImpl.this::makeCellSeparator);
                }

            }

            @ClassName("default-table-cell")
            public class DefaultTableCell extends TextView {

                public DefaultTableCell(String text) {
                    super(text);
                }
            }
        }
    }

    private class RowLayout extends RectangularLayout {

        private final List<? extends Element> cells;
        private final Supplier<Element> cellSeparatorFactory;

        public RowLayout(List<? extends Element> cells, Supplier<Element> cellSeparatorFactory) {
            this.cells = cells;
            this.cellSeparatorFactory = cellSeparatorFactory;
        }

        @Override
        public void enumerateChildren(Consumer<Element> consumer) {
            cells.forEach(consumer);
        }

        @Override
        protected Size preferredSize(BoxConstraints constraints, LayoutContext1 context) {
            if (constraints.min().height() == constraints.max().height())
                return constraints.min();

            // TODO lehessen Table.Columnnak is megadni widthet
            Size size = constraints.clamp(
                    new Size(model.columns.size() * 60, 0)); // TODO ezt css-be kéne rakni

            int separatorThickness = TableView.this.separatorThickness();
            List<Integer> colWidths = colWidths(size.width());

            int h = 0, sumW = 0;
            for (int i = 0; i < cells.size(); i++) {
                int w = colWidths.get(i);
                if (w == 0)
                    throw new IllegalStateException(colWidths.toString());
                sumW += w;
                h = Math.max(h, context.preferredSize(cells.get(i), constraints.withWidth(w, w)).height());
                if (i != cells.size() - 1)
                    sumW += separatorThickness;
            }
            System.out.println(colWidths);
            return new Size(sumW, h);
        }

        @Override
        protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
            List<Integer> colWidths = colWidths(size.width());
            int separatorThickness = separatorThickness();

            int x = 0;
            for (int i = 0; i < cells.size(); i++) {
                int w = colWidths.get(i);
                placer.accept(cells.get(i), new Rectangle(x, 0, w, size.height()));
                x += w;

                if (i != cells.size() - 1) {
                    placer.accept(cellSeparatorFactory.get(),
                            new Rectangle(x, 0, separatorThickness, size.height()));
                }
                x += separatorThickness();
            }
        }
    }
}
