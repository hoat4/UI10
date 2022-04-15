package ui10.image;

import ui10.geom.Point;

import java.util.List;

public record LinearGradient(Point start, Point end, List<Stop> stops) implements Fill {

    public LinearGradient {
        stops = List.copyOf(stops);
    }

    public record Stop(Color color, double fraction) {}
}
