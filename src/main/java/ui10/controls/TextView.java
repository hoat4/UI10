package ui10.controls;

import ui10.base.ContentEditable;
import ui10.base.Element;
import ui10.binding7.InvalidationMark;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Objects;

public class TextView extends ui10.base.ElementModel implements ContentEditable<TextView.StringContentPoint> {

    private String text = "";
    private ContentEditable.ContentRange<StringContentPoint> selection;

    public TextView() {
    }

    public TextView(String text) {
        Objects.requireNonNull(text);
        this.text = text;
        invalidate(TextViewProperty.TEXT);
    }

    public String text() {
        return text;
    }

    public void text(String text) {
        Objects.requireNonNull(text);
        this.text = text;
    }

    @Override
    public Transferable contentAt(ContentRange<StringContentPoint> range) {
        return new StringSelection(text.substring(range.begin().characterOffset(), range.end().characterOffset()));
    }

    @Override
    public StringContentPoint traverse(StringContentPoint point, TraversalDirection direction, TraversalUnit type) {
        if (type != TraversalUnit.CHARACTER)
            throw new UnsupportedOperationException(type + " not supported yet");

        return switch (direction) {
            // TODO surrogate párok kezelése

            case BACKWARD -> point.characterOffset() == 0 ? null
                    : new StringContentPoint(point.characterOffset() - 1, this);

            case FORWARD -> point.characterOffset() == text.length() - 1 ? null
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

        text(s.substring(0, point.characterOffset()) + text + s.substring(point.characterOffset()));
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
        return selection;
    }

    @Override
    public void select(ContentRange<StringContentPoint> range) {
        selection = range;
        invalidate(TextViewProperty.SELECTION);
    }

    @Override
    public StringContentPoint leftEnd() {
        return new StringContentPoint(0, this);
    }

    @Override
    public StringContentPoint rightEnd() {
        return new StringContentPoint(text.length(), this);
    }

    public enum TextViewProperty implements InvalidationMark {

        TEXT, SELECTION
    }

    public record StringContentPoint(int characterOffset, Element element) implements ContentEditable.ContentPoint {

        @Override
        public int compareTo(ContentPoint o) {
            return characterOffset - ((StringContentPoint)o).characterOffset;
        }
    }

}
