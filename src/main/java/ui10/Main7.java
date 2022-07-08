package ui10;

import ui10.base.*;
import ui10.controls.*;
import ui10.controls.dialog.Dialogs;
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

public class Main7 extends LightweightContainer {

    private final TextView label = new TextView("Hello world!");
    private final TextField textField = new TextField("asdf");
    private final Button button = new Button(new TextView("Gomb"), () -> {
        //System.out.println("asdf");
        //textField.text(textField.text()+"X");
        Dialogs.showMessage("Hello world!");
    });

    @Provide
    private final ViewProvider decorableViewProvider = new DecorableControlViewProvider();
    @Provide
    private final StyleProvider styleProvider;

    public static Main7 main;

    public Main7() throws IOException {
        CSSParser css;
        try (Reader r = new InputStreamReader(getClass().getResourceAsStream("/ui10/theme/modena-imitation/modena-imitation.css"))) {
            css = new CSSParser(new CSSScanner(r));
            css.parseCSS();
        }
        this.styleProvider = new StyleProvider(new CSSDecorator(css));
    }

    @Override
    protected Element content() {
        textField.content.text("szövegmező");
        LinearLayout vbox = vertically(
                label,
                button,
                textField
        );
        vbox.gap = 10;

        button.role.set(Button.Role.DEFAULT);

        Element tab1 = centered(vbox);
        Element tab2 = empty();
        TabbedPane.Tab.of(tab1).title("Egyik tab");
        TabbedPane.Tab.of(tab2).title("Táblázat");
        return new FocusBoundary(new TabbedPane(List.of(tab1, tab2)));
    }

    @Override
    public <T> void collect(Class<T> type, Consumer<T> consumer) {
        ProvideHandler.collectProvidedObjects(this, type, consumer);
        super.collect(type, consumer);
    }

    public static void main(String[] args) throws IOException {
        AWTDesktop.instance().windows.add(main = new Main7());
    }
}
