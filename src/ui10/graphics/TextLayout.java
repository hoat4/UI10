package ui10.graphics;

import ui10.font.FontMetrics;
import ui10.geom.Point;

public interface TextLayout {

    int pickTextPos(Point p);

    FontMetrics metrics();
}
