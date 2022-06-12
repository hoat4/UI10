package ui10.di;

import java.util.function.Consumer;

public interface Component {

    <T> void collect(Class<T> type, Consumer<T> consumer);
}
