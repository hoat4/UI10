package ui10.decoration.views;

import ui10.base.Element;
import ui10.controls.Label;
import ui10.controls.TabbedPane;
import ui10.decoration.Style;
import ui10.decoration.StyleableContainer;
import ui10.layout.Layouts;
import ui10.layout.LinearLayout;
import ui10.layout.LinearLayoutBuilder;

import java.util.List;
import java.util.stream.Collectors;

import static ui10.layout.Layouts.*;
import static ui10.layout.Layouts.HorizontalAlignment.LEFT;

public class StyleableTabbedPaneView extends StyleableView<TabbedPane, StyleableTabbedPaneView.TabbedPaneStyle>
        implements TabbedPane.TabbedPaneListener {

    private final TabHeaderArea tabHeaderArea = new TabHeaderArea();

    public StyleableTabbedPaneView(TabbedPane model) {
        super(model);
    }

    @Override
    protected Element contentImpl() {
        return LinearLayoutBuilder.vertical().
                add(0, tabHeaderArea).
                add(1, model.selectedTab()).
                build();
    }

    @Override
    public void tabAdded(Element tab) {
        invalidate();
    }

    @Override
    public void tabRemoved(Element tab) {
        invalidate();
    }

    @Override
    public void selectedTabChanged() {
        invalidate();
    }

    public interface TabbedPaneStyle extends Style {
    }

    public class TabHeaderArea extends StyleableContainer<Style> {

        @Override
        protected Element contentImpl() {
            return Layouts.stack(
                    new TabHeaderBackground(),
                    new TabButtons()
            );
        }
    }

    public class TabButtons extends StyleableContainer<Style> {

        @Override
        protected Element contentImpl() {
            List<TabButton> tabButtons = model.tabs().stream().map(TabButton::new).collect(Collectors.toList());
            LinearLayout hbox = horizontally(tabButtons);
            hbox.gap = 2; // TODO
            return Layouts.halign(LEFT, hbox);
        }
    }

    public class TabHeaderBackground extends StyleableContainer<Style> {

        @Override
        protected Element contentImpl() {
            return empty();
        }
    }

    public class TabButton extends StyleableContainer<Style> {

        private final Element tab;
        private final TabButtonInner tabButtonInner;

        public TabButton(Element tab) {
            this.tab = tab;
            tabButtonInner = new TabButtonInner(tab);
        }

        @Override
        protected Element contentImpl() {
            return tabButtonInner;
        }

        public boolean isSelected() {
            return tab == model.selectedTab();
        }
    }

    public class TabButtonInner extends StyleableContainer<Style> {

        private final Label tabButtonLabel;

        private TabButtonInner(Element tab){
            tabButtonLabel = new Label(TabbedPane.Tab.of(tab).title());
        }

        @Override
        protected Element contentImpl() {
            return tabButtonLabel;
        }
    }
}
