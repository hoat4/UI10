package ui10.decoration.views;

import ui10.base.Container;
import ui10.base.Element;
import ui10.base.EventContext;
import ui10.base.InputHandler;
import ui10.binding.ListChange;
import ui10.binding5.ElementEvent;
import ui10.controls.Label;
import ui10.controls.TabbedPane;
import ui10.controls.TabbedPane.TabSelected;
import ui10.controls.TabbedPane.TabsChanged;
import ui10.decoration.Style;
import ui10.decoration.StyleableContainer;
import ui10.geom.Axis;
import ui10.input.pointer.MouseEvent;
import ui10.layout.Layouts;
import ui10.layout.LinearLayout;
import ui10.layout.LinearLayoutBuilder;

import static ui10.layout.Layouts.HorizontalAlignment.LEFT;

public class StyleableTabbedPaneView extends StyleableView<TabbedPane, StyleableTabbedPaneView.TabbedPaneStyle>
        implements TabbedPane.TabbedPaneListener {

    private LinearLayout<TabButton> tabButtons;
    private final TabPaneContent content = new TabPaneContent();

    public StyleableTabbedPaneView(TabbedPane model) {
        super(model);
    }

    @Setup
    void init() {
        tabButtons = new LinearLayout<>(Axis.HORIZONTAL, model.tabs().streamBinding().map(TabButton::new).toList());
    }

    @Override
    protected Element contentImpl() {
        Element tabHeaderArea = decoration().tabHeaderArea(Layouts.halign(LEFT, decoration().tabButtons(tabButtons)));

        return LinearLayoutBuilder.vertical().
                add(0, tabHeaderArea).
                add(1, content).
                build();
    }

    @Override
    public void tabsChanged(ListChange<Element> change) {
    }

    @Override
    public void selectedTabChanged(Element oldSelectedTab, Element newSelectedTab) {
        if (oldSelectedTab != null)
            tabButton(oldSelectedTab).refresh();
        tabButton(newSelectedTab).refresh();
        content.refresh();
    }

    public void handleModelEvent(ElementEvent event) {
        switch (event) {
            case TabsChanged e -> {
                e.change().applyOn(tabButtons.elements(), TabButton::new);
            }
            case TabSelected e -> {
                if (e.oldValue() != null)
                    tabButton(e.oldValue()).refresh();
                tabButton(e.newValue()).refresh();
                content.refresh();
            }
            default -> {
            }
        }
    }

    private TabButton tabButton(Element tab) {
        return tabButtons.elements().stream().filter(t -> t.tab.equals(tab)).findAny().orElseThrow();
    }

    public interface TabbedPaneStyle extends Style {

        Element tabButtons(LinearLayout<TabButton> element);

        Element tabHeaderArea(Element element);
    }

    public class TabButton extends StyleableContainer<TabButton.TabButtonStyle> implements InputHandler {

        private final Element tab;
        private final Label tabButtonLabel;

        public TabButton(Element tab) {
            this.tab = tab;
            tabButtonLabel = new Label(TabbedPane.Tab.of(tab).title());
        }

        void refresh() {
            decoration().selectedChanged();
        }

        @Override
        protected Element contentImpl() {
            return tabButtonLabel;
        }

        public boolean isSelected() {
            return tab == model.selectedTab();
        }

        @InputHandler.EventHandler
        private void onMousePress(MouseEvent.MousePressEvent event, EventContext eventContext) {
            //focusContext().focusedControl.set(this);
            model.selectedTab(tab);
        }

        public interface TabButtonStyle extends Style {

            void selectedChanged();
        }
    }

    private class TabPaneContent extends Container {

        void refresh() {
            invalidate();
        }

        @Override
        protected Element content() {
            return model.selectedTab();
        }
    }
}
