package ui10.layout;

import ui10.binding.*;
import ui10.geom.Size;
import ui10.nodes.LayoutNode;
import ui10.nodes.Node;

import static ui10.binding.ObservableScalar.binding;

public abstract class OneChildOnePassLayout extends LayoutNode {

    protected final ObservableScalar<? extends Node> content;

    private final EventBus<Void> eventBus = new StandaloneEventBus<>(); // ezt majd ki kéne váltani

    public OneChildOnePassLayout(ObservableScalar<? extends Node> content) {
        super(ObservableList.of(content));
        this.content = content;
        initState();
    }

    protected void initState() {
    }

    protected void dependsOn(Observable<?>... observables) {
        for (Observable<?> o : observables)
            o.subscribe(e -> {
                initState();
                eventBus.postEvent(null);
            });
    }

    @Override
    protected ObservableScalar<Size> makeLayoutThread(ObservableScalar<BoxConstraints> in,
                                                      boolean apply, Scope scope) {
        ObservableScalar<BoxConstraints> childIn = in.map(this::childConstraints);
        ObservableScalar<Size> childOut = content.flatMap(n -> n.layoutThread(childIn, apply, null)); // TODO scope

        Binding<Size> b = binding(in, childOut, (constraints, contentSize) ->
                layout(constraints, content.get(), contentSize, apply));
        eventBus.subscribe(v->b.refresh());
        return b;
    }

    protected abstract BoxConstraints childConstraints(BoxConstraints constraints);

    protected abstract Size layout(BoxConstraints constraints, Node content, Size contentSize, boolean apply);
}
