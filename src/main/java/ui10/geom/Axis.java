package ui10.geom;

public enum Axis {
    HORIZONTAL, VERTICAL;

    public Axis other() {
        if (this == HORIZONTAL)
            return VERTICAL;
        else
            return HORIZONTAL;
    }
}
