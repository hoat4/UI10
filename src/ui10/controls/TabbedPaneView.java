package ui10.controls;

import ui10.base.*;
import ui10.binding2.ChangeEvent;
import ui10.binding2.ElementEvent;
import ui10.binding2.Property;
import ui10.input.pointer.MouseEvent;

import java.util.List;
import java.util.Set;

import static ui10.decoration.css.CSSClass.withClass;
import static ui10.layout.Layouts.*;
import static ui10.layout.Layouts.HorizontalAlignment.LEFT;

public class TabbedPaneView extends ControlView<TabbedPane> {

    private final List<TabButton> tabButtons;

    public TabbedPaneView(TabbedPane model) {
        super(model);
        tabButtons = model.tabs.stream().map(TabButton::new).toList();
        model.activeTab().getProperty(TabButton.TAB_BUTTON_PROPERTY).setProperty(Button.PRESSED_PROPERTY, true);
    }

    @Override
    protected Set<Property<?>> modelPropertySubscriptions() {
        return Set.of(TabbedPane.ACTIVE_TAB_PROPERTY);
    }

    @Override
    protected void handleModelEvent(ElementEvent event) {
        if (event instanceof ChangeEvent e && e.property().equals(TabbedPane.ACTIVE_TAB_PROPERTY)) {
            if (e.oldValue() != null)
                ((Element) e.oldValue()).getProperty(TabButton.TAB_BUTTON_PROPERTY).setProperty(Button.PRESSED_PROPERTY, false);
            if (e.newValue() != null)
                ((Element) e.newValue()).getProperty(TabButton.TAB_BUTTON_PROPERTY).setProperty(Button.PRESSED_PROPERTY, true);

            invalidateContent();
        }
    }

    @Override
    protected Element content() {
        return vertically(
                tabHeaderArea(),
                model.activeTab()
        );
    }

    private Element tabHeaderArea() {
        return withClass("tab-header-area", stack(
                withClass("tab-header-area-background", empty()),
                halign(LEFT, withClass("tab-buttons", horizontally(tabButtons)))
        ));
    }

    private class TabButton extends Control {

        private static final Property<TabButton> TAB_BUTTON_PROPERTY = new Property<>(false);

        private final Element tab;

        public TabButton(Element tab) {
            this.tab = tab;
            tab.setProperty(TAB_BUTTON_PROPERTY, this); // who should delete this property if tab is removed?
        }

        @EventHandler
        private void onClick(MouseEvent.MousePressEvent event, EventContext eventContext) {
            model.activeTab(tab);
        }

        @Override
        protected Element content() {
            return wrapWithClass("tab-button-inner",
                    withClass("tab-button-label", new Label(Tabs.title(tab))));
        }

        @Override
        public String elementName() {
            return "TabButton";
        }
    }

}
