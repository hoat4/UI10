/*
package ui10.controls;

import ui10.base.ControlModel;
import ui10.base.Element;
import ui10.binding2.Property;

import java.util.List;

public class TabbedPane extends ControlModel {

    public static final Property<Element> ACTIVE_TAB_PROPERTY = new Property<>(false);

    public final List<Element> tabs;

    public TabbedPane(List<Element> tabs) {
        this.tabs = List.copyOf(tabs);

        activeTab(tabs.get(0));
        view = new TabbedPaneView(this);
    }

    public Element activeTab() {
        return getProperty(ACTIVE_TAB_PROPERTY);
    }

    public void activeTab(Element tab) {
        setProperty(ACTIVE_TAB_PROPERTY, tab);
    }
}
*/