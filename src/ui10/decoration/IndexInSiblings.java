package ui10.decoration;

import ui10.base.Attribute;

import java.util.Objects;

public class IndexInSiblings extends Attribute {

    public final int index;

    public IndexInSiblings(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexInSiblings that = (IndexInSiblings) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}
