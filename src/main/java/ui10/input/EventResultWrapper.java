package ui10.input;

import ui10.base.Element;

public record EventResultWrapper<R extends EventInterpretation.EventResponse>(
        Element responder, EventInterpretation<R> eventInterpretation, R response) {
}
