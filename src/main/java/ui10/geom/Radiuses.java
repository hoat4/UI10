package ui10.geom;

public record Radiuses(int topLeft, int topRight, int bottomLeft, int bottomRight) {
    public boolean allZero() {
        return topLeft == 0 && topRight == 0 && bottomLeft == 0 && bottomRight == 0;
    }
}
