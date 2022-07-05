package ui10.controls;

import ui10.base.Element;
import ui10.base.ElementExtra;
import ui10.binding9.OList;
import ui10.binding9.OVal;

import java.util.ArrayList;
import java.util.List;

public class TabbedPane extends ui10.base.ElementModel {

    public final OVal<Element> selectedTab = new OVal<>();
    public final OList<Element> tabs = new OList<>(new ArrayList<>()) {
        @Override
        protected void onWrite() {
            if (selectedTab.get() == null || !tabs.contains(selectedTab.get()))
                selectedTab(tabs.isEmpty() ? null : tabs.get(0));

            super.onWrite();
        }
    };

    public TabbedPane() {
    }

    public TabbedPane(List<? extends Element> tabs) {
        this.tabs.addAll(tabs);
    }

    public List<Element> tabs() {
        return tabs;
    }

    public Element selectedTab() {
        return selectedTab.get();
    }

    public void selectedTab(Element selectedTab) {
        this.selectedTab.set(selectedTab);
    }

    // TODO:  ;


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
