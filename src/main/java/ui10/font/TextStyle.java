package ui10.font;

import ui10.graphics.TextLayout;

public interface TextStyle {

    FontMetrics textSize(String text);

    int height();
}
