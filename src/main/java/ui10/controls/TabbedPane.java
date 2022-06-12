package ui10.controls;

import ui10.base.Element;
import ui10.base.ElementExtra;
import ui10.base.ElementModel;
import ui10.base.EnduringElement;
import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;
import ui10.window.Window;

import java.util.List;

public class TabbedPane extends ElementModel<TabbedPane.TabbedPaneListener> {

    private final ObservableList<EnduringElement> tabs = new ObservableListImpl<>(ObservableList.simpleListSubscriber(
            this::tabAdded, this::tabRemoved));

    private Element selectedTab;

    public TabbedPane() {
    }

    public TabbedPane(List<? extends EnduringElement> tabs) {
        this.tabs.addAll(tabs);
    }

    public List<EnduringElement> tabs() {
        return tabs;
    }

    public Element selectedTab() {
        return selectedTab;
    }

    public void selectedTab(Element selectedTab) {
        if (selectedTab == this.selectedTab)
            return;
        this.selectedTab = selectedTab;
        listener().selectedTabChanged();
    }

    private void tabAdded(Element tab) {
        selectedTab(tabs.get(0));
        listener().tabAdded(tab);
    }

    private void tabRemoved(Element tab) {
        selectedTab(tabs.isEmpty() ? null : tabs.get(0));
        listener().tabRemoved(tab);
    }

    public interface TabbedPaneListener extends ElementModelListener {

        void tabAdded(Element tab);

        void tabRemoved(Element tab);

        void selectedTabChanged();
    }

    public static class Tab extends ElementExtra {

        private String title;

        public void title(String title) {
            this.title = title;
        }

        public String title() {
            return title;
        }

        public static Tab of(EnduringElement element) {
            Tab t = element.extra(Tab.class);
            if (t == null)
                element.extras.add(t = new Tab());
            return t;
        }
    }
}
