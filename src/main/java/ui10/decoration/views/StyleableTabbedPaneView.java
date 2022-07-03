package ui10.decoration.views;

import ui10.base.*;
import ui10.controls.TextView;
import ui10.controls.TabbedPane;
import ui10.decoration.Style;
import ui10.decoration.StyleableContainer;
import ui10.geom.Axis;
import ui10.input.pointer.MouseEvent;
import ui10.layout.Layouts;
import ui10.layout.LinearLayout;
import ui10.layout.LinearLayoutBuilder;

import java.util.stream.Collectors;

import static ui10.layout.Layouts.HorizontalAlignment.LEFT;

public class StyleableTabbedPaneView extends StyleableView<TabbedPane, StyleableTabbedPaneView.TabbedPaneStyle> implements ui10.binding7.InvalidationListener {

    private LinearLayout<TabButton> tabButtons = new LinearLayout<>(Axis.HORIZONTAL);
    private final TabPaneContent content = new TabPaneContent();
    private TabButton prevSelected;

    public StyleableTabbedPaneView(TabbedPane model) {
        super(model);
    }

    @Override
    protected void validateImpl() {
        if (model.dirtyProperties().contains(TabbedPane.TabPaneProperty.TABS)) {
            tabButtons.elements().clear();
            tabButtons.elements().addAll(model.tabs().stream().map(TabButton::new).collect(Collectors.toList()));
        }
        if (model.dirtyProperties().contains(TabbedPane.TabPaneProperty.SELECTED_TAB)) {
            if (prevSelected != null)
                prevSelected.refresh();
            (prevSelected = tabButton(model.selectedTab())).refresh();
            content.refresh();
        }
    }

    @Override
    protected Element contentImpl() {
        Element tabHeaderArea = decoration().tabHeaderArea(Layouts.halign(LEFT, decoration().tabButtons(tabButtons)));

        return LinearLayoutBuilder.vertical().
                add(0, tabHeaderArea).
                add(1, content).
                build();
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
        private final TextView tabButtonLabel;

        public TabButton(Element tab) {
            this.tab = tab;
            tabButtonLabel = new TextView(TabbedPane.Tab.of(tab).title());
        }

        void refresh() {
            if (decoration() != null)
                decoration().selectedChanged();
        }

        @Override
        protected Element content() {
            return new MouseTarget(super.content()) {
                @Override
                public DragHandler handlePress(MouseEvent.MousePressEvent event) {
                    model.selectedTab(tab);
                    return null;
                }
            };
        }

        @Override
        protected Element contentImpl() {
            return tabButtonLabel;
        }

        public boolean isSelected() {
            return tab == model.selectedTab();
        }

        public interface TabButtonStyle extends Style {

            void selectedChanged();
        }
    }

    private class TabPaneContent extends Container {

        void refresh() {
            invalidateContainer();
        }

        @Override
        protected Element content() {
            return model.selectedTab();
        }
    }
}
