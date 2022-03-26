package ui10.controls;

import ui10.base.Element;
import ui10.base.LayoutContext1;
import ui10.base.LayoutContext2;
import ui10.base.Pane;
import ui10.binding2.Property;
import ui10.decoration.IndexInSiblings;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.layout.Layouts;
import ui10.layout.RectangularLayout;
import ui10.layout.Wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static ui10.layout.Layouts.vertically;

public class TableView<T> extends Pane {

    public static final Property<Integer> ROW_HEIGHT_PROPERTY = new Property<>(true);

    // ha nem lehet szerkeszteni, lehetne TableColumn<? super T> is
    private final List<? extends TableColumn<T>> columns;
    private final TableHeader header = new TableHeader();

    private final List<? extends T> data;
    private List<Integer> colWidths = new ArrayList<>();

    // ha lehet szerkeszteni, List<T> legyen
    public TableView(List<? extends TableColumn<T>> columns, List<? extends T> data) {
        this.columns = List.copyOf(columns);
        this.data = data;
    }

    @Override
    protected void validate() {
        super.validate();
        header.validate();
    }

    @Override
    protected Element content() {
        return new Wrapper(Layouts.vertically(
                header,
                new TableBody()
        )) {
            @Override
            protected Shape computeContentShape(Shape containerShape, LayoutContext2 context) {
                colWidths.clear();
                for (int i = 0; i < columns.size(); i++)
                    colWidths.add(containerShape.bounds().width() / columns.size());

                return super.computeContentShape(containerShape, context);
            }
        };
    }

    @Override
    public String elementName() {
        return "Table";
    }

    public static class TableColumn<T> {

        public final String caption;
        public final Function<T, String> getter;

        public TableColumn(String caption, Function<T, String> getter) {
            this.caption = caption;
            this.getter = getter;
        }
    }

    private class TableHeader extends Pane {

        private final List<TableColumnHeader> colHeaders = new ArrayList<>();

        @Override
        protected void validate() {
            super.validate();
            colHeaders.clear();
            for (TableColumn<?> column : columns)
                colHeaders.add(new TableColumnHeader(column));
        }

        @Override
        protected Element content() {
            return new RowLayout(colHeaders);
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

            for (int i = 1, y = 0; y < size.height(); y += rowHeight) {
                TableRow row = new TableRow();
                row.index = i - 1;
                row.attributes().add(new IndexInSiblings(i++));
                // ???
                row.initParent(this);
                placer.accept(row, new Rectangle(0, y, size.width(), rowHeight));
            }
        }
    }

    private class TableRow extends Pane {

        int index;

        @Override
        public String elementName() {
            return "TableRow";
        }

        @Override
        protected Element content() {
            if (data.size() <= index)
                return Layouts.empty();

            T obj = data.get(index);

            List<Element> cells = new ArrayList<>();
            for (TableColumn<T> col : columns) {
                cells.add(new Label(col.getter.apply(obj)));
            }
            return new RowLayout(cells);
        }
    }

    private class RowLayout extends RectangularLayout {

        private final List<? extends Element> cells;

        public RowLayout(List<? extends Element> cells) {
            this.cells = cells;
        }

        @Override
        public void enumerateStaticChildren(Consumer<Element> consumer) {
            cells.forEach(consumer);
        }

        @Override
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            if (constraints.min().height() == constraints.max().height())
                return constraints.min();

            int h = 0, sumW = 0;
            for (int i = 0; i < cells.size(); i++) {
                int w = colWidths.get(i);
                sumW += w;
                h = Math.max(h, context.preferredSize(cells.get(i), constraints.withWidth(w, w)).height());
            }
            return new Size(sumW, h);
        }

        @Override
        protected void doPerformLayout(Size size, BiConsumer<Element, Rectangle> placer, LayoutContext1 context) {
            int x = 0;
            for (int i = 0; i < cells.size(); i++) {
                int w = colWidths.get(i);
                placer.accept(cells.get(i), new Rectangle(x, 0, w, size.height()));
                x += w;
            }
        }
    }

}
