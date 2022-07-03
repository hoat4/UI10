package ui10.base;

import ui10.binding7.InvalidationMark;

import java.util.Objects;

public abstract class Container extends ElementModel {

    private boolean valid;
    protected boolean contentValid;

    protected void validate() {
        contentValid = false;
    }

    protected abstract Element content();

    private Element cachedContent;

    // ez nem tudom hogy jó-e ha publikus, egyelőre a view miatt muszáj annak lennie
    public Element getContent() {
        if (!valid) {
            validate();

            if (!contentValid || cachedContent == null) {
                cachedContent = Objects.requireNonNull(content(), () -> "null content in " + this);
                contentValid = true;
            }

            valid = true;
        }

        return cachedContent;
    }

    public void invalidateContainer() {
        valid = false;
        invalidate(ContainerProperties.CONTENT);
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

    public enum ContainerProperties implements InvalidationMark {

        CONTENT
    }
}
