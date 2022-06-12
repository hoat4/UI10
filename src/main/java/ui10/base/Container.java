package ui10.base;

import java.util.Objects;

public abstract class Container extends ElementModel<Container.ContainerListener> {

    private boolean valid;

    protected void validate() {
    }

    protected abstract Element content();

    private Element cachedContent;

    // ez nem tudom hogy jó-e ha publikus, egyelőre a view miatt muszáj annak lennie
    public Element getContent() {
        if (!valid) {
            validate();

            cachedContent = Objects.requireNonNull(content(), () -> "null content in " + this);

            valid = true;
        }

        return cachedContent;
    }

    public void invalidate() {
        valid = false;
        listener().contentChanged();
    }

    public static Container of(Element node) {
        if (node instanceof Container p)
            return p;
        else
            return new Container() {
                @Override
                public Element content() {
                    return node;
                }

                @Override
                public String toString() {
                    return "Container[" + node + ", parent="+parent+"]";
                }
            };
    }

    public interface ContainerListener extends ElementModelListener {

        void contentChanged();
    }
}
