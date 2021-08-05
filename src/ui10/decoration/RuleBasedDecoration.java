package ui10.decoration;

import ui10.binding.Scope;
import ui10.nodes.Node;
import ui10.nodes.Pane;

import java.util.List;
import java.util.Map;

public class RuleBasedDecoration extends Decoration {

    public final Map<Tag, List<Decoration>> rules;

    public RuleBasedDecoration(Map<Tag, List<Decoration>> rules) {
        this.rules = rules;
    }

    @Override
    public Node decorateInner(Pane control, Node content, Scope scope) {
        decorateRecursively(content, scope);

        for (Tag t : control.tags()) {
            List<Decoration> decorations = this.rules.get(t);
            if (decorations != null)
                for (Decoration d : decorations)
                    content = d.decorateInner(control, content, scope);
        }

        return content;
    }

    @Override
    public Node decorateOuter(Pane pane, Node content, Scope scope) {
        for (Tag t : pane.tags()) {
            List<Decoration> decorations = this.rules.get(t);
            if (decorations != null)
                for (Decoration d : decorations)
                    content = d.decorateOuter(pane, content, scope);
        }

        return content;
    }

    private void decorateRecursively(Node node, Scope scope) {
        // TODO faváltozások figyelése
        if (node instanceof Pane p)
            p.decorations().add(this); // TODO scope
        else
            for (Node n : node.children())
                decorateRecursively(n, scope);
    }
}
