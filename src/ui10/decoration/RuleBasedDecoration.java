package ui10.decoration;

import ui10.binding.ObservableList;
import ui10.binding.Scope;
import ui10.pane.Frame;
import ui10.pane.Pane;

import java.util.List;
import java.util.Map;

public class RuleBasedDecoration extends Decoration {

    public final Map<Tag, List<Decoration>> rules;

    public RuleBasedDecoration(Map<Tag, List<Decoration>> rules) {
        this.rules = rules;
    }

    @Override
    public Pane decorateContent(Pane container, Pane content, Scope scope) {
        if (container instanceof Decorable d) {
            for (Object o : container.extendedProperties().keySet()) {
                if (!(o instanceof Tag t))
                    continue;

                List<Decoration> decorations = this.rules.get(t);
                if (decorations != null) {
                    d.decorations().addAll(decorations);
                    // TODO a miénket kéne itt törölni akkor is, ha egy decoration már amúgy is hozzá volt adva
                    scope.onClose(() -> d.decorations().removeAll(decorations));
                }
            }
        }

        ObservableList<? extends Frame> children = container.children();
        if (children != null)
            for (Frame frame : children) {
                frame.pane().transformations().add(this);
                scope.onClose(() -> frame.pane().transformations().remove(this));
            }

        if (!(container instanceof Decorable)) {
            for (Object o : container.extendedProperties().keySet()) {
                if (!(o instanceof Tag t))
                    continue;

                List<Decoration> decorations = this.rules.get(t);
                if (decorations != null) {
                    for (Decoration d : decorations) {
                        content = d.decorateContent(container, content, scope);
                    }
                }
            }
        }

        return content;
    }

    @Override
    public Pane decorateContainer(Pane container, Scope scope) {
        for (Object o : container.extendedProperties().keySet()) {
            if (!(o instanceof Tag t))
                continue;

            List<Decoration> decorations = this.rules.get(t);
            if (decorations != null) {
                for (Decoration d : decorations) {
                    container = d.decorateContainer(container, scope);
                }
            }
        }

        return container;
    }
}
