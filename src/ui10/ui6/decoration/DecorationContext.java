package ui10.ui6.decoration;

public class DecorationContext {

    public int length(Length length) {
        return length.px() >> 14;
    }
}
