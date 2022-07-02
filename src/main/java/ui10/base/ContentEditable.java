package ui10.base;

import java.awt.datatransfer.Transferable;
import java.util.Objects;

public interface ContentEditable<P extends ContentEditable.ContentPoint> {

    Transferable contentAt(ContentRange<P> range);

    /**
     * @return {@code null} if there are no content in the specified direction
     */
    P traverse(P point, TraversalDirection direction, TraversalUnit type);

    ContentRange<P> insert(P point, Transferable content);

    default ContentRange<P> replace(ContentRange<P> range, Transferable content) {
        P p = delete(range);
        return insert(p, content);
    }

    P delete(ContentRange<P> selection);

    // ezt a kettőt majd tüntessük el innen
    ContentRange<P> selection();

    void select(ContentRange<P> range);

    P leftEnd();

    P rightEnd();

    enum TraversalDirection {
        BACKWARD, FORWARD
    }

    enum TraversalUnit {

        CHARACTER, WORD, LINE
    }

    record ContentRange<P extends ContentPoint>(P begin, P end) {
        public ContentRange {
            Objects.requireNonNull(begin);
            Objects.requireNonNull(end);
        }
    }

    interface ContentPoint extends Comparable<ContentPoint> {

        Element element();

        static <P extends ContentPoint> P min(P a, P b) {
            return a.compareTo(b) <= 0 ? a : b;
        }

        static <P extends ContentPoint> P max(P a, P b) {
            return a.compareTo(b) > 0 ? a : b;
        }
    }
}
