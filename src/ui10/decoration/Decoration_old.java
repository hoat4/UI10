/*
package ui10.decoration;

import ui10.binding.PropertyHolder;
import ui10.binding.ScalarProperty;
import ui10.binding.Scope;
import ui10.nodes.Pane;
import ui10.nodes.Node;

import java.util.function.BiConsumer;

public abstract class Decoration extends PropertyHolder {

    private boolean valid;

    public ScalarProperty<Boolean> valid() {
        return property((Decoration d) -> d.valid, (d, v) -> d.valid = v);
    }

    public abstract Node decorateInner(Pane control, Node content, Scope scope);

    public abstract Node decorateOuter(Pane control, Node content, Scope scope);

    protected void invalidate() {
        valid().set(false);
    }

    public static Decoration ofModify(BiConsumer<Pane, Scope> f) {
        return new Decoration() {

            @Override
            public Node decorateInner(Pane control, Node content, Scope scope) {
                f.accept(control, scope);
                return content;
            }

            @Override
            public Node decorateOuter(Pane container, Node content, Scope scope) {
                return content;
            }
        };
    }

    public static Decoration ofReplace(ReplacerDecorator f) {
        return new Decoration() {
            @Override
            public Node decorateInner(Pane control, Node content, Scope scope) {
                return f.decorateContent(control, content, scope);
            }

            @Override
            public Node decorateOuter(Pane container, Node content, Scope scope) {
                return content;
            }
        };
    }

    @FunctionalInterface
    public interface ReplacerDecorator {
        Node decorateContent(Node container, Node content, Scope scope);
    }

}
*/