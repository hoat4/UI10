package ui10.controls;

import ui10.base.ContentEditable;
import ui10.base.Element;
import ui10.binding9.OVal;
import ui10.geom.Point;
import ui10.geom.shape.Shape;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TextView extends Element implements ContentEditable<TextView.StringContentPoint> {

    public final OVal<String> text = new OVal<>() {
        @Override
        protected String normalize(String value) {
            return value == null ? "" : value;
        }
    };
    public final OVal<ContentRange<StringContentPoint>> selection = new OVal<>(null);

    public TextView() {
    }

    public TextView(String text) {
        this.text.set(text);
    }

    public String text() {
        return text.get();
    }

    public void text(String text) {
        this.text.set(text);
    }

    @Override
    public Transferable contentAt(ContentRange<StringContentPoint> range) {
        return new StringSelection(text.get().substring(range.begin().characterOffset(), range.end().characterOffset()));
    }

    @Override
    public StringContentPoint traverse(StringContentPoint point, TraversalDirection direction, TraversalUnit type) {
        if (type != TraversalUnit.CHARACTER)
            throw new UnsupportedOperationException(type + " not supported yet");

        return switch (direction) {
            // TODO surrogate párok kezelése

            case BACKWARD -> point.characterOffset() == 0 ? null
                    : new StringContentPoint(point.characterOffset() - 1, this);

            case FORWARD -> point.characterOffset() == text.get().length() - 1 ? null
                    : new StringContentPoint(point.characterOffset() + 1, this);
        };
    }

    @Override
    public ContentRange<StringContentPoint> insert(StringContentPoint point, Transferable content) {
        String s;

        try {
            s = (String) content.getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e); // ???
        }

        String currentText = text();
        text(currentText.substring(0, point.characterOffset()) + s + currentText.substring(point.characterOffset()));
        return new ContentRange<>(point, new StringContentPoint(point.characterOffset() + s.length(), this));
    }

    @Override
    public StringContentPoint delete(ContentRange<StringContentPoint> selection) {
        text(text().substring(0, selection.begin().characterOffset())
                + text().substring(selection.end().characterOffset()));
        return selection.begin();
    }

    @Override
    public ContentRange<StringContentPoint> selection() {
        return selection.get();
    }

    @Override
    public void select(ContentRange<StringContentPoint> range) {
        selection.set(range);
    }

    @Override
    public StringContentPoint leftEnd() {
        return new StringContentPoint(0, this);
    }

    @Override
    public StringContentPoint rightEnd() {
        return new StringContentPoint(text.get().length(), this);
    }

    public record StringContentPoint(int characterOffset, Element element) implements ContentEditable.ContentPoint {

        @Override
        public int compareTo(ContentPoint o) {
            return characterOffset - ((StringContentPoint)o).characterOffset;
        }
    }

}
