package ui10.decoration.views;

import ui10.base.Container;
import ui10.base.Element;
import ui10.binding9.OVal;
import ui10.controls.TabbedPane;
import ui10.controls.TextView;
import ui10.decoration.StyleableContainer;
import ui10.geom.Axis;
import ui10.input.EventInterpretation;
import ui10.layout.Layouts;
import ui10.layout.LinearLayout;
import ui10.layout.LinearLayoutBuilder;

import java.util.Objects;
import java.util.stream.Collectors;

import static ui10.layout.Layouts.HorizontalAlignment.LEFT;

public class StyleableTabbedPaneView extends StyleableView<TabbedPane> {

    private final TabPaneContent content = new TabPaneContent();
    private final TabHeaderArea tabHeaderArea = new TabHeaderArea();
    private TabButton prevSelected;

    public StyleableTabbedPaneView(TabbedPane model) {
        super(model);
    }

    @Override
    protected Element contentImpl() {
        return LinearLayoutBuilder.vertical().
                add(0, tabHeaderArea).
                add(1, content).
                build();
    }

    @ElementName("TabHeaderArea")
    public class TabHeaderArea extends StyleableContainer {
        private LinearLayout<TabButton> tabButtons = new LinearLayout<>(Axis.HORIZONTAL);

        @RepeatedInit
        void initTabButtons() {
            // TODO ezeket külön kéne szedni, mert selectedTab sokkal gyakrabban változik mint tabButtons

            tabButtons.elements().clear();
            tabButtons.elements().addAll(model.tabs().stream().map(TabButton::new).collect(Collectors.toList()));

            if (prevSelected != null)
                prevSelected.selected.set(false);
            (prevSelected = tabButton(model.selectedTab())).selected.set(true);
        }

        @Override
        protected Element contentImpl() {
            tabButtons.gap = 2; // TODO
            return Layouts.stack(
                    new TabHeaderBackground(),
                    Layouts.halign(LEFT, new TabButtons())
            );
        }


        private TabButton tabButton(Element tab) {
            return tabButtons.elements().stream().filter(t -> t.tab.equals(tab)).findAny().orElseThrow();
        }

        @ElementName("TabHeaderBackground")
        public class TabHeaderBackground extends StyleableContainer{

            @Override
            protected Element contentImpl() {
                return Layouts.empty();
            }
        }

        @ElementName("TabButtons")
        public class TabButtons extends StyleableContainer{

            @Override
            protected Element contentImpl() {
                return tabButtons;
            }
        }
    }

    public class TabButton extends StyleableContainer {

        private final Element tab;
        private final TextView tabButtonLabel;
        public final OVal<Boolean> selected= new OVal<>(false);

        public TabButton(Element tab) {
            this.tab = tab;
            tabButtonLabel = new TabButtonLabel(TabbedPane.Tab.of(tab).title());
        }

        @EventHandler
        private void press(EventInterpretation.BeginPress beginPress) {
            model.selectedTab(tab);
        }

        @Override
        protected Element contentImpl() {
            return tabButtonLabel;
        }

        @ClassName("tab-button-label")
        public static class TabButtonLabel extends TextView {
            public TabButtonLabel(String text) {
                super(text);
            }
        }
    }

    private class TabPaneContent extends Container {

        @Override
        protected Element content() {
            Element element = model.selectedTab();
            Objects.requireNonNull(element);
            return element;
        }
    }
}
