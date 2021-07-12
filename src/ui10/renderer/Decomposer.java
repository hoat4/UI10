package ui10.renderer;

import ui10.binding.ListChange;
import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;
import ui10.node.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class Decomposer {

    private final Node root;
    private final Set<Class<? extends Node>> primitives;
    private final Map<Class<? extends Node>, Function<? extends Node, ? extends Node>> transformers;

    private final ObservableList<Node> nodes = new ObservableListImpl<>();
    private final Map<Node, Node> transformedNodes = new HashMap<>();

    private final Consumer<ListChange<Node>> listChangeConsumer = this::onListChange;

    public Decomposer(Node root, Set<Class<? extends Node>> primitives,
                      Map<Class<? extends Node>, Function<? extends Node, ? extends Node>> transformers) {
        this.root = root;
        this.primitives = primitives;
        this.transformers = transformers;
        add(root);
    }

    private void onListChange(ListChange<Node> evt) {
        if (evt instanceof ListChange.ListAdd<Node> a)
            a.elements().forEach(this::add);
        else if (evt instanceof ListChange.ListRemove<Node> r)
            r.elements().forEach(this::remove);
        else
            throw new IllegalArgumentException(evt.toString());
    }

    private <N extends Node> void add(N node) {
        if (primitives.contains(node.getClass()))
            nodes.add(node);
        else if (transformers.containsKey(node.getClass())) {
            Function<N, ? extends Node> transformer =
                    (Function<N, ? extends Node>) transformers.get(node.getClass());

            Node newNode = transformer.apply(node);
            transformedNodes.put(node, newNode);
            add(newNode);
        } else {
            ObservableList<Node> c = node.children();
            if (c == null)
                throw new UnsupportedOperationException("not a primitive and not transformable: " + node);
            c.enumerateAndSubscribe(listChangeConsumer);
        }
    }

    private void remove(Node node) {
        if (primitives.contains(node.getClass()))
            nodes.remove(node);
        else if (transformedNodes.containsKey(node))
            remove(transformedNodes.remove(node));
        else
            node.children().unsubscribe(listChangeConsumer);
    }

    public ObservableList<Node> nodes() {
        return nodes;
    }
}

