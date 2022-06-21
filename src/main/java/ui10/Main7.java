package ui10;

import ui10.base.*;
import ui10.controls.Button;
import ui10.controls.Label;
import ui10.controls.TabbedPane;
import ui10.controls.TextField;
import ui10.decoration.StyleProvider;
import ui10.decoration.css.CSSDecorator;
import ui10.decoration.css.CSSParser;
import ui10.decoration.css.CSSScanner;
import ui10.decoration.views.DecorableControlViewProvider;
import ui10.di.Provide;
import ui10.di.ProvideHandler;
import ui10.layout.LinearLayout;
import ui10.shell.awt.AWTDesktop;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.function.Consumer;

import static ui10.layout.Layouts.*;

public class Main7 extends Container {

    private final Label label = new Label("Hello world!");
    private final Button button = new Button("Gomb", () -> System.out.println("asdf"));
    private final TextField textField = new TextField();

    @Provide
    private final ViewProvider decorableViewProvider = new DecorableControlViewProvider();
    @Provide
    private final StyleProvider styleProvider;
    @Provide
    private final FocusContext focusContext = new FocusContext();

    public Main7() throws IOException {
        CSSParser css;
        try (Reader r = new InputStreamReader(getClass().getResourceAsStream("modena-imitation.css"))) {
            css = new CSSParser(new CSSScanner(r));
            css.parseCSS();
        }
        this.styleProvider = new StyleProvider(new CSSDecorator(css));
    }

    @Override
    protected Element content() {
        textField.text("szövegmező");
        LinearLayout vbox = vertically(
                new Label("label"),
                button,
                textField
        );
        vbox.gap = 10;

        Element tab1 = (Element) centered(vbox);
        Element tab2 = (Element) empty();
        TabbedPane.Tab.of(tab1).title("Egyik tab");
        TabbedPane.Tab.of(tab2).title("Táblázat");
        return new TabbedPane(List.of(tab1, tab2));
    }

    @Override
    public <T> void collect(Class<T> type, Consumer<T> consumer) {
        ProvideHandler.collectProvidedObjects(this, type, consumer);
        super.collect(type, consumer);
    }

    public static void main(String[] args) throws IOException {
        AWTDesktop.instance().windows.add(new Main7());
    }
}
