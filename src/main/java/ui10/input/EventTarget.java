package ui10.input;

import ui10.base.Element;
import ui10.controls.Button;
import ui10.decoration.views.StyleableButtonView;
import ui10.geom.Point;

public interface EventTarget {

    boolean appliesTo(Element element);

    record PointTarget(Point point) implements EventTarget{
        @Override
        public boolean appliesTo(Element element) {
            return element.hasShape() && element.shape().contains(point);
        }
    }

    record ButtonRoleTarget(Button.Role buttonRole) implements EventTarget{

        @Override
        public boolean appliesTo(Element element) {
            // TODO
            return element instanceof StyleableButtonView btn && btn.model.role.get() == buttonRole;
        }
    }

    record ElementTarget(Element element) implements EventTarget {

        @Override
        public boolean appliesTo(Element element) {
            return element.renderableElement() == this.element.renderableElement();
        }
    }
}
