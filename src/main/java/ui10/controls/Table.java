package ui10.controls;

import ui10.base.ElementModel;

import java.util.List;
import java.util.function.Function;

// "Table" element name
public class Table<T> extends ElementModel<ElementModel.ElementModelListener> {

    // ha nem lehet szerkeszteni, lehetne TableColumn<? super T> is
    public final List<? extends Column<T>> columns;

    public final List<? extends T> data;

    // ha lehet szerkeszteni, List<T> legyen
    public Table(List<? extends Column<T>> columns, List<? extends T> data) {
        if (columns.isEmpty())
            throw new IllegalArgumentException();

        this.columns = List.copyOf(columns);
        this.data = data;
    }

    public static class Column<T> {

        public final String caption;
        public final Function<T, String> getter;

        public Column(String caption, Function<T, String> getter) {
            this.caption = caption;
            this.getter = getter;
        }
    }
}
