package ui10.control4.controls;

import ui10.base.ElementModel;

import java.util.List;
import java.util.function.Function;

public abstract class Table<T> extends ElementModel<Table.TableListener> {

    public record Column<T, V>(String title, Function<T, V> function) {
    }

    public abstract List<Column<T, ?>> columns();

    public abstract List<T> data();

    public interface TableListener extends ElementModel.ElementModelListener {

        void columnsChanged();

        void cellChanged(int x, int y);
    }
}
