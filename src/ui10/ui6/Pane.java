package ui10.ui6;

import ui10.binding.PropertyEvent;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.layout.LayoutResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public abstract class Pane extends RenderableElement {

    private final List<RenderableElement> children = new ArrayList<>();
    private boolean valid;

    public BiConsumer<Pane, Element> decorator;
    public FocusContext focusContext;

    protected void validate() {
    }

    protected abstract Element content();

    private Element getContent() {
        if (!valid) {
            validate();
            valid = true;
        }

        Element content = Objects.requireNonNull(content(), () -> "null content in " + this);
        if (decorator != null)
            decorator.accept(this, content);
        return content;
    }

    public List<RenderableElement> renderableElements() {
        if (!valid)
            onShapeApplied(shape, null, layoutDependencies);
        return children;
    }

    private boolean valid2;

    @Override
    public <T extends PropertyEvent> void onChange(T changeEvent) {
        super.onChange(changeEvent);
        valid2 = false;
        rendererData.eventLoop().runLater(()->{
            if (valid2)
                return;
            valid2 = true;

            for (LayoutResult lr : layoutDependencies)
                if (!preferredShape(((PaneLR)lr.obj()).inputConstraints).shape().equals(lr.shape())) {
                    rendererData.invalidateLayout();
                    break;
                }

            this.invalidatePane();

        });
    }

    /**
     * Invalidates layout and content.
     */
    protected void invalidatePane() {
        valid = false;
        if (rendererData != null) {
            rendererData.invalidateRendererData(); // miért?
        }
    }

    @Override
    protected LayoutResult preferredShapeImpl(BoxConstraints constraints) {
        LayoutResult lr = getContent().preferredShape(constraints);
        return new LayoutResult(lr.shape(), this, new PaneLR(constraints, lr));
    }

    @Override
    protected void onShapeApplied(Shape lr, LayoutContext context, List<LayoutResult> dependencies) {
        List<LayoutResult> contentLayoutDeps = dependencies.stream().map(l -> ((PaneLR) l.obj()).contentLR).toList();

        children.clear();
        getContent().performLayout(lr, e -> {
            children.add(e);
            if (e instanceof Pane p)
                p.focusContext = focusContext;
        }, contentLayoutDeps);
    }

    public static Pane of(Element node) {
        if (node instanceof Pane p)
            return p;
        else
            return new Pane() {
                @Override
                public Element content() {
                    return node;
                }
            };
    }

    private record PaneLR(BoxConstraints inputConstraints, LayoutResult contentLR) {
    }
}
