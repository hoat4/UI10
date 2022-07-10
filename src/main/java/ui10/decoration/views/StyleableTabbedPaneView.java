package ui10.decoration.views;

import ui10.base.Container;
import ui10.base.Element;
import ui10.binding9.OVal;
import ui10.controls.TabbedPane;
import ui10.controls.TextView;
import ui10.decoration.Style;
import ui10.decoration.StyleableContainer;
import ui10.geom.Axis;
import ui10.input.Event;
import ui10.input.EventInterpretation;
import ui10.layout.Layouts;
import ui10.layout.LinearLayout;
import ui10.layout.LinearLayoutBuilder;

import java.util.stream.Collectors;

import static ui10.binding9.Bindings.repeatIfInvalidated;
import static ui10.layout.Layouts.HorizontalAlignment.LEFT;

public class StyleableTabbedPaneView extends StyleableView<TabbedPane, StyleableTabbedPaneView.TabbedPaneStyle> {

    private LinearLayout<TabButton> tabButtons = new LinearLayout<>(Axis.HORIZONTAL);
    private final TabPaneContent content = new TabPaneContent();
    private TabButton prevSelected;

    public StyleableTabbedPaneView(TabbedPane model) {
        super(model);
    }

    @RepeatedInit
    void initTabButtons() {
        tabButtons.elements().clear();
        tabButtons.elements().addAll(model.tabs().stream().map(TabButton::new).collect(Collectors.toList()));
    }

    @RepeatedInit
    void initSelectedTab() {
        if (prevSelected != null)
            prevSelected.selected.set(false);
        (prevSelected = tabButton(model.selectedTab())).selected.set(true);
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

    public class TabButton extends StyleableContainer<Style> {

        private final Element tab;
        private final TextView tabButtonLabel;
        public final OVal<Boolean> selected= new OVal<>(false);

        public TabButton(Element tab) {
            this.tab = tab;
            tabButtonLabel = new TextView(TabbedPane.Tab.of(tab).title());
        }

        @EventHandler
        private void press(EventInterpretation.BeginPress beginPress) {
            model.selectedTab(tab);
        }

        @Override
        protected Element contentImpl() {
            return tabButtonLabel;
        }
    }

    private class TabPaneContent extends Container {

        @Override
        protected Element content() {
            return model.selectedTab();
        }
    }
}
