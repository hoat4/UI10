package ui10.controls;

import ui10.base.Element;
import ui10.base.LayoutContext1;
import ui10.base.LayoutContext2;
import ui10.base.Pane;
import ui10.decoration.DecorationContext;
import ui10.decoration.IndexInSiblings;
import ui10.decoration.css.CSSProperty;
import ui10.decoration.css.Styleable;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.layout.Layouts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static ui10.layout.Layouts.VerticalAlignment.TOP;
import static ui10.layout.Layouts.vertically;

public class TableView extends Pane implements Styleable {

    private final List<? extends TableColumn<?>> columns;
    private final TableHeader header = new TableHeader();

    public TableView(List<? extends TableColumn<?>> columns) {
        this.columns = List.copyOf(columns);
    }

    @Override
    protected void validate() {
        super.validate();
        header.validate();
    }

    @Override
    protected Element content() {
        List<Element> elems = new ArrayList<>();
        elems.add(header);
        for (int i = 0; i < 10; i++) {
            TableRow row = new TableRow();
            row.attributes().add(new IndexInSiblings(i+1));
            elems.add(row);
        }
        return Layouts.valign(TOP, vertically(elems));
    }

    @Override
    public String elementName() {
        return "Table";
    }

    @Override
    public <T> void setProperty(CSSProperty<T> property, T value, DecorationContext decorationContext) {
    }

    public static class TableColumn<T> {

        public final String caption;
        public final Function<T, String> getter;

        public TableColumn(String caption, Function<T, String> getter) {
            this.caption = caption;
            this.getter = getter;
        }
    }

    private class TableHeader extends Pane implements Styleable {

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
            return Layouts.horizontally(colHeaders);
        }

        @Override
        public String elementName() {
            return "TableHeader";
        }

        @Override
        public <T> void setProperty(CSSProperty<T> property, T value, DecorationContext decorationContext) {
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

    private class TableRow extends Element implements Styleable{

        @Override
        protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
            return constraints.min();
        }

        @Override
        protected void performLayoutImpl(Shape shape, LayoutContext2 context) {

        }

        @Override
        public void enumerateStaticChildren(Consumer<Element> consumer) {

        }

        @Override
        public String elementName() {
            return "TableRow";
        }

        @Override
        public <T> void setProperty(CSSProperty<T> property, T value, DecorationContext decorationContext) {
        }
    }
}
