package ui10.decoration;

import ui10.nodes.Pane;

public record Tag(String name) {
    @SuppressWarnings("unchecked")
    public static <P extends Pane> P tag(P pane, Tag tag) {
        pane.tags().add(tag);
        return pane;
    }
}
