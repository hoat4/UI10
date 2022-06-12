package ui10.base;

import ui10.layout.Layouts;

public enum TextAlign {

    LEFT(Layouts.HorizontalAlignment.LEFT),
    CENTER(Layouts.HorizontalAlignment.CENTER),
    RIGHT(Layouts.HorizontalAlignment.RIGHT);

    final Layouts.HorizontalAlignment asHAlign;

    TextAlign(Layouts.HorizontalAlignment asHAlign) {
        this.asHAlign = asHAlign;
    }
}
