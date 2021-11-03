package ui10.ui6;

import ui10.binding.PropertyEvent;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.ui6.layout.LayoutContext1;
import ui10.ui6.layout.LayoutContext2;

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
            onShapeApplied(shape, new LayoutContext2.AbstractLayoutContext2() {

                @Override
                public void accept(RenderableElement element) {
                    throw new UnsupportedOperationException();
                }
            });
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

            final LayoutContext1 consumer = (pane, dep) -> {
                // these known shapes could be used later to avoid computing preferred shapes redundantly
            };

            for (LayoutContext1.LayoutDependency dep : layoutDependencies) {
                if (!preferredShape(dep.inputConstraints(), consumer).equals(dep.shape())) {
                    rendererData.invalidateLayout();
                    break;
                }
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
    protected Shape preferredShapeImpl(BoxConstraints constraints, LayoutContext1 context) {
        return getContent().preferredShape(constraints, context);
    }

    @Override
    protected void onShapeApplied(Shape shape, LayoutContext2 context) {
        children.clear();
        getContent().performLayout(shape, new LayoutContext2() {
            @Override
            public void accept(RenderableElement e) {
                children.add(e);
                if (e instanceof Pane p)
                    p.focusContext = focusContext;
            }

            @Override
            public List<LayoutDependency> getDependencies(RenderableElement element) {
                return context.getDependencies(element);
            }

            @Override
            public void addLayoutDependency(RenderableElement element, LayoutDependency d) {
                context.addLayoutDependency(element, d);
            }
        });
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
}
