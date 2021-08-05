package ui10.font;

import ui10.geom.Num;
import ui10.geom.Size;

public interface TextStyle {

    FontMetrics textSize(String text);

    Num height();
}
