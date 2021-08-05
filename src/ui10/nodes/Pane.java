package ui10.nodes;

import ui10.binding.*;
import ui10.decoration.Decoration;
import ui10.geom.Size;
import ui10.layout.BoxConstraints;

import java.util.Objects;
import java.util.function.Consumer;

import static ui10.geom.Point.ORIGO;

public abstract class Pane extends Node {

    private ObservableScalar<? extends Node> undecoratedContent;
    private final ScalarProperty<Node> decoratedContent = ScalarProperty.create();
    private Scope decorationScope;

    private ObservableList<Decoration> decorations;

    public final ObservableList<Decoration> decorations() {
        if (decorations == null) {
            decorations = new ObservableListImpl<>();
            decorations.subscribe(ObservableList.simpleListSubscriber(d -> {
                d.valid().subscribe(new DecorationInvalidationSubscriber());
                invalidateDecorations();
            }, d -> {
                d.valid().unsubscribe(new DecorationInvalidationSubscriber());
                invalidateDecorations();
            }));
        }
        return decorations;
    }

    private void invalidateDecorations() {
        initDecoratedContent();
    }

    private void initDecoratedContent() {
        if (decorationScope != null)
            decorationScope.close();
        decorationScope = new Scope();

        if (undecoratedContent == null) {
            undecoratedContent = Objects.requireNonNull(paneContent());
            undecoratedContent.subscribe(e -> invalidateDecorations());
        }

        Node p = undecoratedContent.get();
        for (Decoration decoration : decorations())
            p = decoration.decorateInner(this, p, decorationScope);
        p = wrapDecoratedContent(p);
        for (Decoration decoration : decorations())
            p = decoration.decorateOuter(this, p, decorationScope);
        decoratedContent.set(p);
    }

    @Override
    public ObservableList<? extends Node> children() {
        return ObservableList.of(content());
    }

    private ObservableScalar<Node> content() {
        if (decoratedContent.get() == null)
            initDecoratedContent();
        return decoratedContent;
    }

    protected abstract ObservableScalar<? extends Node> paneContent();

    protected Node wrapDecoratedContent(Node decoratedContent) {
        return decoratedContent;
    }

    @Override
    protected ObservableScalar<Size> makeLayoutThread(ObservableScalar<BoxConstraints> in, boolean apply, Scope scope) {
        ObservableScalar<Size> t = content().flatMap(n -> n.layoutThread(in, apply, scope)); // TODO scope
        if (apply)
            content().getAndSubscribe(n -> {
                n.position.set(ORIGO);
            });
        return t;
    }

    private class DecorationInvalidationSubscriber implements Consumer<ChangeEvent<Boolean>> {

        @Override
        public void accept(ChangeEvent<Boolean> e) {
            if (!e.newValue())
                invalidateDecorations();
        }
    }
}
