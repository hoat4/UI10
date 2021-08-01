package ui10.decoration;

import ui10.binding.PropertyHolder;
import ui10.binding.PropertyTransformation;
import ui10.binding.ScalarProperty;
import ui10.binding.Scope;
import ui10.pane.Pane;

import java.util.function.BiConsumer;

public abstract class Decoration extends PropertyHolder implements PropertyTransformation<Pane> {

    private boolean valid;

    @Override
    public ScalarProperty<Boolean> valid() {
        return property((Decoration d) -> d.valid, (d, v) -> d.valid = v);
    }

    @Override
    public Pane apply(Pane pane, Scope scope) {
        return decorateContent(pane, pane, scope);
    }

    public abstract Pane decorateContent(Pane container, Pane content, Scope scope);

    public abstract Pane decorateContainer(Pane container, Scope scope);

    protected void invalidate() {
        valid().set(false);
    }

    public static Decoration ofModify(BiConsumer<Pane, Scope> f) {
        return new Decoration() {

            @Override
            public Pane decorateContent(Pane container, Pane content, Scope scope) {
                f.accept(container, scope);
                return content;
            }

            @Override
            public Pane decorateContainer(Pane container, Scope scope) {
                return container;
            }
        };
    }

    public static Decoration ofReplace(ReplacerDecorator f) {
        return new Decoration() {
            @Override
            public Pane decorateContent(Pane container, Pane content, Scope scope) {
                return f.decorateContent(container, content, scope);
            }

            @Override
            public Pane decorateContainer(Pane container, Scope scope) {
                return container;
            }
        };
    }

    @FunctionalInterface
    public interface ReplacerDecorator {
        Pane decorateContent(Pane container, Pane content, Scope scope);
    }

}
