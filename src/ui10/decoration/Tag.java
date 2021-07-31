package ui10.decoration;

import ui10.nodes2.Pane;

public record Tag(String name) {
    @SuppressWarnings("unchecked")
    public static <P extends Pane> P tag(P pane, Tag tag) {
        pane.extendedProperties().put(tag, Boolean.TRUE);
        return pane;
    }
}
