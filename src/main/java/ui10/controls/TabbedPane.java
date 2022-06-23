package ui10.controls;

import ui10.base.ElementExtra;
import ui10.base.ElementModel;
import ui10.base.Element;
import ui10.binding.ListChange;
import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;
import ui10.binding5.ElementEvent;

import java.util.List;

public class TabbedPane extends ElementModel<TabbedPane.TabbedPaneListener> {

    private final ObservableList<Element> tabs = new ObservableListImpl<>(this::tabsChanged);

    private Element selectedTab;

    public TabbedPane() {
    }

    public TabbedPane(List<? extends Element> tabs) {
        this.tabs.addAll(tabs);
    }

    public ObservableList<Element> tabs() {
        return tabs;
    }

    public Element selectedTab() {
        return selectedTab;
    }

    public void selectedTab(Element selectedTab) {
        if (selectedTab == this.selectedTab)
            return;
        Element old = this.selectedTab;
        this.selectedTab = selectedTab;
        listener().selectedTabChanged(old, selectedTab);
    }

    private void tabsChanged(ListChange<Element> change) {
        listener().tabsChanged(change);
        selectedTab(tabs.isEmpty() ? null : tabs.get(0));
    }

    public record TabsChanged(TabbedPane source, ListChange<Element> change) implements ElementEvent {
    }

    public record TabSelected(TabbedPane source, Element oldValue, Element newValue) implements ElementEvent.ChangeEvent<Element> {
    }

    @FunctionalInterface
    interface ChangeEventFactory<SRC, T, E extends ElementEvent.ChangeEvent<T>> {

        E makeChangeEvent(SRC source, T oldValue, T newValue);
    }

    public interface TabbedPaneListener extends ElementModelListener {

        void tabsChanged(ListChange<Element> change);

        void selectedTabChanged(Element oldSelectedTab, Element newSelectedTab);
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
