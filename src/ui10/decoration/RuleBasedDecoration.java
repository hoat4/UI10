package ui10.decoration;

import ui10.binding.ObservableScalar;
import ui10.binding.Scope;
import ui10.nodes2.Pane;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class RuleBasedDecoration extends Decoration {

    public final Map<Tag, List<Decoration>> rules;

    public RuleBasedDecoration(Map<Tag, List<Decoration>> rules) {
        this.rules = rules;
    }

    @Override
    public Pane apply(Pane pane, Scope scope) {
        for (Object o : pane.extendedProperties().keySet()) {
            if (!(o instanceof Tag t))
                continue;

            List<Decoration> rules = this.rules.get(t);
            for (Decoration d : rules)
                pane = d.apply(pane, scope);
        }

        return pane;
    }

}
