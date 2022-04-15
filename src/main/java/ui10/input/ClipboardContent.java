package ui10.input;

import java.util.Set;

public interface ClipboardContent {

    Set<ContentType<?>> contentTypes();

    <T> T content(ContentType<T> contentType);
}
