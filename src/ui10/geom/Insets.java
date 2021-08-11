package ui10.geom;

public record Insets(int top, int right, int bottom, int left) {

    public Insets(int all) {
        this(all, all);
    }

    public Insets(int topBottom, int leftRight) {
        this(topBottom, leftRight, topBottom, leftRight);
    }

    public int horizontal() {
        return left+right;
    }
    public int vertical() {
        return top+bottom;
    }
}
