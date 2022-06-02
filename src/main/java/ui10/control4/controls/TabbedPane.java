package ui10.control4.controls;

import ui10.base.Element;
import ui10.base.ElementModel;

import java.util.List;

public abstract class TabbedPane extends ElementModel<TabbedPane.TabbedPaneListener> {

    private Element selectedTab;

    public abstract List<Tab> tabs();

    public Element selectedTab() {
        return selectedTab;
    }

    public void selectedTab(Element selectedTab) {
        this.selectedTab = selectedTab;
        listener().selectedTabChanged();
    }

    public record Tab(String title, Element content) {}

    public interface TabbedPaneListener extends ElementModelListener {

        void selectedTabChanged();
    }
}
