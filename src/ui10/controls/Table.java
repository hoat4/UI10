package ui10.controls;

import ui10.base.ControlModel;

import java.util.List;
import java.util.function.Function;

public class Table<T> extends ControlModel {

    // ha nem lehet szerkeszteni, lehetne TableColumn<? super T> is
    public final List<? extends TableColumn<T>> columns;

    public final List<? extends T> data;

    // ha lehet szerkeszteni, List<T> legyen
    public Table(List<? extends TableColumn<T>> columns, List<? extends T> data) {
        if (columns.isEmpty())
            throw new IllegalArgumentException();

        this.columns = List.copyOf(columns);
        this.data = data;

        view = new TableView<>(this);
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
}
