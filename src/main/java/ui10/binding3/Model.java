package ui10.binding3;

import ui10.binding2.ElementEvent;

import java.util.function.Consumer;

public interface Model {

    void subscribe(Consumer<? super ElementEvent> consumer, PropertyIdentifier... properties);
    // vagy csak a view subscribe-olhasson?

    // prefetch? bár lehet hogy overkill

}
