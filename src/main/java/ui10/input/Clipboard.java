package ui10.input;

import ui10.binding.ObservableScalar;

public interface Clipboard {

    ObservableScalar<ClipboardContent> content();

    // history?
}
