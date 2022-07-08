package ui10.input;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class  Event {

    private final Instant timestamp;

    // TODO ez ne legyen publikus, de most Element::dispatchEvent-nek kell egyelőre
    public List<EventInterpretation<?>> interpretations = new ArrayList<>();

    public Event(EventInterpretation<?> nativeInterpretation) {
        this.timestamp = Instant.now();
        this.interpretations.add(nativeInterpretation);
    }

    public Event(List<EventInterpretation<?>> nativeInterpretations) {
        this.timestamp = Instant.now();
        this.interpretations.addAll(nativeInterpretations);
    }

    public List<EventInterpretation<?>> interpretations() {
        return Collections.unmodifiableList(interpretations);
    }
}
