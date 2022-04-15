package ui10.controls;

import ui10.base.Element;
import ui10.binding2.Property;

public class Tabs {

    public static final Property<String> TITLE_PROPERTY = new Property<>(false, "");

    public static String title(Element tab) {
        return tab.getProperty(TITLE_PROPERTY);
    }

    public static void title(Element tab, String title) {
        tab.setProperty(TITLE_PROPERTY, title);
    }
}
