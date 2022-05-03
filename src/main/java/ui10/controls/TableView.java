package ui10.controls;

import ui10.base.*;
import ui10.binding2.ElementEvent;
import ui10.binding2.Property;
import ui10.controls.Table.TableColumn;
import ui10.decoration.DecorationContext;
import ui10.decoration.Fill;
import ui10.decoration.IndexInSiblings;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;
import ui10.layout.Layouts;
import ui10.layout.RectangularLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static ui10.decoration.css.CSSClass.withClass;

public class TableView<T> extends ControlView<Table<T>> {

    public static final Property<Integer> ROW_HEIGHT_PROPERTY = new Property<>(true);

    // ez egyelőre Fill, majd valami border specnek kéne lennie
    public static final Property<Fill> CELL_SEPARATOR_PROPERTY = new Property<>(false);

    private static final int SEPARATOR_THICKNESS = 1;

    private final TableHeader header = new TableHeader();

    public TableView(Table<T> model) {
        super(model);
    }

    @Override
    protected Set<Property<?>> modelPropertySubscriptions() {
        return Set.of();
    }

    @Override
    protected void handleModelEvent(ElementEvent event) {
    }

    @Override
    public String elementName() {
        return "TableView";
    }

    @Override
    protected Element content() {
        return Layouts.vertically(
                header,
                new TableBody()
        );
    }

    private List<Integer> colWidths(int containerWidth) {
        // TODO handle empty col list

        List<Integer> colWidths = new ArrayList<>();
        int availWidth = containerWidth - (model.columns.size() - 1) * SEPARATOR_THICKNESS;
        for (int i = 0; i < model.columns.size(); i++) {
            int w = availWidth / (model.columns.size() - i);
            colWidths.add(w);
            availWidth -= w;
        }
        return colWidths;
    }

    private class TableHeader extends Container {

        private final List<TableColumnHeader> colHeaders = new ArrayList<>();

        @Override
        protected void validate() {
            super.validate();
            colHeaders.clear();

//            List<Integer> colWidths = colWidths(getShapeOrFail().bounds().width());

            int x = 0;
            for (int i = 0; i < model.columns.size(); i++) {
                TableColumn<T> col = model.columns.get(i);
                colHeaders.add(new TableColumnHeader(col));

                //              int w = colWidths.get(i);
                //            x += w;
                if (i != model.columns.size() - 1) {
                    x += SEPARATOR_THICKNESS;

                    // dragger
                }
            }
        }

        @Override
        protected Element content() {
            return new RowLayout(colHeaders, getProperty(CELL_SEPARATOR_PROPERTY));
        }

        @Override
        public String elementName() {
            return "TableHeader";
        }

    }

    private class TableColumnHeader extends Label {

        private final TableColumn<?> column;

        public TableColumnHeader(TableColumn<?> column) {
            super(column.caption);
            this.column = column;
        }

        @Override
        protected void validate() {
            text(column.caption);
            super.validate();
        }

        @Override
        public String elementName() {
            return "TableColumnHeader";
        }
    }

    private class TableBody extends RectangularLayout {

        @Override
        public String elementName() {
            return "TableBody";
        }

        @Override
        public void enumerateStaticChildren(Consumer<Element> consumer) {
        }

        @Override
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            return constraints.min();
        }

        @Override
        protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
            int rowHeight = TableView.this.getProperty(ROW_HEIGHT_PROPERTY);

            for (int i = 1, y = 0; y < size.height(); y += rowHeight + SEPARATOR_THICKNESS) {
                TableRow row = new TableRow();
                row.index = i - 1;
                row.setProperty(IndexInSiblings.INDEX_IN_SIBLINGS_PROPERTY, i++);
                // ???
                row.initParent(this);
                placer.accept(row, new Rectangle(0, y, size.width(), rowHeight));

                placer.accept(cellSeparator().makeElement(new DecorationContext(this)),
                        new Rectangle(0, y + rowHeight, size.width(), SEPARATOR_THICKNESS));
            }
        }

        // legyen külön a TableBodyba és TableRowba?
        private Fill cellSeparator() {
            return TableBody.this.getProperty(CELL_SEPARATOR_PROPERTY);
        }

        private class TableRow extends Container {

            int index;

            @Override
            public String elementName() {
                return "TableRow";
            }

            @Override
            protected Element content() {
                if (model.data.size() <= index) {
                    // kellenek cellseparatorok is ilyenkor
                    return new RowLayout(IntStream.range(0, model.columns.size()).
                            mapToObj(i -> Layouts.empty()).toList(), cellSeparator());
                }

                T obj = model.data.get(index);

                List<Element> cells = new ArrayList<>();
                for (TableColumn<T> col : model.columns) {
                    cells.add(new DefaultTableCell(col.getter.apply(obj)));
                }
                return new RowLayout(cells, cellSeparator());
            }

        }

        private class DefaultTableCell extends Label {
            public DefaultTableCell(String text) {
                super(text);
                withClass("default-table-cell", this); // elemnév vs class?
            }

        }
    }

    private class RowLayout extends RectangularLayout {

        private final List<? extends Element> cells;
        private final Fill borderFill;

        public RowLayout(List<? extends Element> cells, Fill separatorFill) {
            this.cells = cells;
            this.borderFill = separatorFill;
        }

        @Override
        public void enumerateStaticChildren(Consumer<Element> consumer) {
            cells.forEach(consumer);
        }

        @Override
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            if (constraints.min().height() == constraints.max().height())
                return constraints.min();

            // TODO lehessen TableColumnnak is megadni widthet
            Size size = constraints.clamp(
                    new Size(model.columns.size() * 60, 0)); // TODO ezt css-be kéne rakni

            List<Integer> colWidths = colWidths(size.width());

            int h = 0, sumW = 0;
            for (int i = 0; i < cells.size(); i++) {
                int w = colWidths.get(i);
                if (w == 0)
                    throw new IllegalStateException(colWidths.toString());
                sumW += w;
                h = Math.max(h, context.preferredSize(cells.get(i), constraints.withWidth(w, w)).height());
                if (i != cells.size() - 1)
                    sumW += SEPARATOR_THICKNESS;
            }
            System.out.println(colWidths);
            return new Size(sumW, h);
        }

        @Override
        protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
            List<Integer> colWidths = colWidths(size.width());

            int x = 0;
            for (int i = 0; i < cells.size(); i++) {
                int w = colWidths.get(i);
                placer.accept(cells.get(i), new Rectangle(x, 0, w, size.height()));
                x += w;

                if (i != cells.size() - 1)
                    placer.accept(borderFill.makeElement(new DecorationContext(this)),
                            new Rectangle(x, 0, SEPARATOR_THICKNESS, size.height()));
                x += SEPARATOR_THICKNESS;
            }
        }
    }
}
