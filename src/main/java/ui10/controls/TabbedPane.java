package ui10.controls;

import ui10.base.ElementExtra;
import ui10.base.Element;
import ui10.binding.ListChange;
import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;
import ui10.binding7.PropertyBasedModel;

import java.util.List;

public class TabbedPane extends PropertyBasedModel<TabbedPane.TabPaneProperty> {

    private final ObservableList<Element> tabs = new ObservableListImpl<>(this::tabsChanged);

    private Element selectedTab;

    public TabbedPane() {
    }

    public TabbedPane(List<? extends Element> tabs) {
        this.tabs.addAll(tabs);
        invalidate(TabPaneProperty.TABS);
    }

    public ObservableList<Element> tabs() {
        return tabs;
    }

    public Element selectedTab() {
        return selectedTab;
    }

    public void selectedTab(Element selectedTab) {
        if (selectedTab != this.selectedTab) {
            this.selectedTab = selectedTab;
            invalidate(TabPaneProperty.SELECTED_TAB);
        }
    }

    private void tabsChanged(ListChange<Element> change) {
        invalidate(TabPaneProperty.TABS);
        if (selectedTab == null || change.oldElements().contains(selectedTab))
            selectedTab(tabs.isEmpty() ? null : tabs.get(0));
    }

    public enum TabPaneProperty {

        TABS, SELECTED_TAB
    }

    public static class Tab extends ElementExtra {

        private String title;

        public void title(String title) {
            this.title = title;
        }

        public String title() {
            return title;
        }

        public static Tab of(Element element) {
            Tab t = element.extra(Tab.class);
            if (t == null)
                element.extras.add(t = new Tab());
            return t;
        }
    }
}
